# Зарядка для джависта

Как-то раз на одном из программистских форумов я наткнулся на интересную задачку. Интересна она была
тем, что требовалось рекурсивное решение на Java. Мне захотелось разобраться...

## Условие

Написать обобщённый метод, который принимает в качестве аргумента коллекцию объектов и модифицирует
её следующим образом:

1. Если в коллекции встречается только один объект, оставить его как есть.
2. Если в коллекции встречаются подряд два одинаковых объекта и более, удалить их все и поместить на
   это место `null`.
3. Метод должен быть рекурсивным.

Примеры:

* `[1, 2, 2]` → `[1, null]`
* `["dog", "cat", "cat", "fish"]` → `["dog", null, "fish"]`
* `[1, 2, 1, 3, 1]` → `[1, 2, 1, 3, 1]`

## Процедура или функция?

Очевидно, что настоящий метод (то есть предназначенный для вызова на объекте) нам не понадобится.
Хватит статического. По сути это не метод даже, а процедура. Или не процедура? Сложно сказать...

Коллекцию можно обойти только с помощью итератора (под коллекцией понимаем
интерфейс `java.util.Collection`, а не реализацию, например, `java.util.ArrayList`). Единственная
доступная операция изменения – удаление элемента. Причём если один итератор задействован для чтения
из коллекции, а другой используется для удаления, попеременное выполнение этих действий даже в одном
потоке
[недопустимо](https://dev.java/learn/api/collections-framework/iterating/#iterate-and-update).

Пусть мы каким-то образом обошли это ограничение (на самом деле, способ есть, но он не решает
принципиальной проблемы, о которой чуть ниже). Мы удалили из исходной коллекции несколько одинаковых
идущих подряд элементов. Как мы добавим в коллекцию наш заместитель, то есть `null`?

Итераторы такой возможности не предоставляют. А метод `add()` из интерфейса `Collection` нам не
поможет. В случае списка, новый элемент добавляется в конец. В случае множества, зависит от
реализации. Так, у `LinkedHashSet` – в конец, у `HashSet` – неизвестно куда.

Править исходную коллекцию «на лету» не получится. Значит, процедура не годится, нужна функция.

## Уточнённое условие

Требуется написать обобщённую функцию, которая принимает в качестве аргумента коллекцию объектов,
обходит её и складывает элементы в список таким образом, что:

1. Если в исходной коллекции встречается только один объект, добавить его в выходной список.
2. Если в исходной коллекции встречаются подряд два одинаковых объекта и более, добавить в выходной
   список лишь одно значение `null`.
3. Функция должна быть рекурсивной.

Среди элементов исходной коллекции не должно быть `null`. Попытка сравнить такое значение с другим
потерпит неудачу, а именно приведёт к `NullPointerException`. Элементы сравниваются посредством
метода `equals()`, а не оператора сравнения. К слову, `null` в Java – такое же значение, как и все
остальные (в отличие от
[SQL](https://ru.wikipedia.org/wiki/NULL_(SQL)#%D0%9E%D0%BF%D0%B5%D1%80%D0%B0%D1%86%D0%B8%D0%B8_%D1%81_NULL),
например).

По отношению к множеству задача вообще не имеет смысла, ибо множество не содержит дубликатов.
Множество в качестве исходной коллекции – это вырожденный случай.

## Цикл

Решение с циклом проще, чем с рекурсией. Рассмотрим сначала его:

```java
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class LoopSolution {
    public static <T> List<T> cleanCollection(Collection<T> input) {
        if (input.size() < 2)
            return List.copyOf(input);

        List<T> output = new ArrayList<>();

        Iterator<T> tail = input.iterator();
        T previousItem = tail.next();
        int duplicatesCount = 0;
        do {
            T currentItem = tail.next();
            if (currentItem.equals(previousItem)) {
                duplicatesCount++;
            } else {
                T mergedItem = merged(duplicatesCount, previousItem);
                output.add(mergedItem);
                previousItem = currentItem;
                duplicatesCount = 0;
            }
        } while (tail.hasNext());

        T lastItem = merged(duplicatesCount, previousItem);
        output.add(lastItem);

        return output;
    }

    private static <T> T merged(int duplicatesCount, T previousItem) {
        return duplicatesCount == 0
                ? previousItem
                : null;
    }
}
```

Запоминаем первый элемент. Затем идём по коллекции до тех пор, пока не встретится значение, отличное
от первого. Заодно подсчитываем число дубликатов. Благодаря счётчику становится ясно, нужно ли
объединять одинаковые элементы в `null` или нет. Добавляем результат в выходной список,  
берём текущий элемент в качестве первого и мотаем цикл дальше.

Когда цикл завершается, за концом коллекции ничего не остаётся, смена значений не происходит. Этот
случай обрабатываем отдельно.

## Рекурсия

Рекурсивное решение похоже на циклическое. Просто к той части исходной коллекции, которая осталась
после смены значений, функция применяется ещё раз. Последний «кусочек» обрабатывается отдельно, но
несколько иначе по сравнению с циклом.

Поскольку рекурсивная функция принимает в качестве аргументов предыдущее значение и итератор, для
удобства использования она «завёрнута» в обычную.

```java
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.Collections.unmodifiableList;

public class RecursiveSolution {
    public static <T> List<T> cleanCollection(Collection<T> input) {
        if (input.isEmpty())
            return emptyList();

        Iterator<T> tail = input.iterator();

        return cleanTail(
                tail.next(),
                tail);
    }

    private static <T> List<T> cleanTail(T previousItem,
                                         Iterator<T> tail) {
        if (!tail.hasNext())
            return singletonList(previousItem);

        int duplicatesCount = 0;
        do {
            T currentItem = tail.next();
            if (currentItem.equals(previousItem)) {
                duplicatesCount++;
            } else {
                List<T> output = new ArrayList<>();

                T mergedItem = duplicatesCount == 0
                        ? previousItem
                        : null;

                output.add(mergedItem);

                List<T> remainingItems = cleanTail(currentItem, tail);
                output.addAll(remainingItems);

                return unmodifiableList(output);
            }
        } while (tail.hasNext());

        return singletonList(null);
    }
}
```

## Заключение

Хорошая задача мне попалась: наполовину алгоритмическая, наполовину Java-специфическая. Самое то,
чтобы потренироваться.

## PS

Если вдруг кому-то интересны тесты:

```java
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class EverySolutionTest {

    @ParameterizedTest
    @MethodSource("data")
    void loopSolutionTest(List<Integer> expected, List<Integer> input) {
        Collection<Integer> actual = LoopSolution.cleanCollection(input);
        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @MethodSource("data")
    void recursiveSolutionTest(List<Integer> expected, List<Integer> input) {
        Collection<Integer> actual = RecursiveSolution.cleanCollection(input);
        assertEquals(expected, actual);
    }

    static Stream<Arguments> data() {
        return Stream.of(
                arguments(
                        emptyList(),
                        emptyList()),

                arguments(
                        singletonList("a"),
                        singletonList("a")),

                arguments(
                        singletonList(null),
                        asList("42", "42")),

                arguments(
                        asList(1, null),
                        asList(1, 2, 2)),

                arguments(
                        asList(1, 2, 1, 3, 1),
                        asList(1, 2, 1, 3, 1)),

                arguments(
                        asList(null, null, null),
                        asList(1, 1, 2, 2, 3, 3)),

                arguments(
                        asList(2, null, 3),
                        asList(2, 4, 4, 3)));
    }
}
```
