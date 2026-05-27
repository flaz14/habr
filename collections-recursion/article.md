Как-то раз на одном из программистских форумов я наткнулся на интересную задачку. Интересна она была тем, что
требовалось рекурсивное решение на Java. Мне захотелось разобраться...

## Условие

Написать обобщённый метод, который принимает в качестве аргумента коллекцию объектов и модифицирует её следующим
образом:

1. Если в коллекции встречается только один объект, оставить его как есть.
2. Если в коллекции встречаются подряд два одинаковых объекта и более, удалить их все и поместить на это место `null`.
3. Метод должен быть рекурсивным.

Примеры:

* `[1, 2, 2]` → `[1, null]`
* `["dog", "cat", "cat", "fish"]` → `["dog", null, "fish"]`
* `[1, 2, 1, 3, 1]` → `[1, 2, 1, 3, 1]`

## Процедура или функция?

Очевидно, что настоящий метод (то есть предназначенный для вызова на объекте) нам не понадобится. Хватит статического.
По сути это никакой не метод, а самая обычная процедура. Или нет? Сложно сказать...

Коллекцию можно обойти только с помощью итератора (под коллекцией понимаем интерфейс `java.util.Collection`, а не
конкретную реализацию, например, `java.util.ArrayList`). Причем единственная возможная модификация коллекции через
итератор - удаление элемента. Причём если один итератор задействован для обхода коллекции, а другой - для удаления, даже
попеременное выполнение этих операций в одном потоке недопустимо.

```java
public class ConcurrentModificationExample {
    public static void main(String[] args) {
        Set<Integer> hashSet = new HashSet<>();
        hashSet.add(41);
        hashSet.add(19);
        hashSet.add(43);
        hashSet.add(13);

        var iterator1 = hashSet.iterator();
        var iterator2 = hashSet.iterator();

        iterator1.next();
        iterator2.next();
        iterator2.remove();
        iterator1.next();
    }
}
```

Запустим код из листинга 1 и увидим ошибку:

```
Exception in thread "main" java.util.ConcurrentModificationException
	at java.base/java.util.HashMap$HashIterator.nextNode(HashMap.java:1606)
	at java.base/java.util.HashMap$KeyIterator.next(HashMap.java:1629)
	at ConcurrentModificationExample.main(ConcurrentModificationExample.java:19)
```

Допустим, мы каким-то образом обошли это ограничение (на самом деле, способ есть, но он не решает принципиальной
проблемы, о которой чуть позже). Мы удалили из исходной коллекции несколько одинаковых идущих подряд элементов. Как мы
добавим в коллекцию наш заместитель, то есть `null`? Итераторы такой возможности не предоставляют.

Тут вполне резонно возражение, как же так? Ведь в интерфейсе `Collection` есть метод `add()`. Вот только непонятно, куда
добавится элемент. В случае списка, в конец. В случае множества - зависит от реализации. Так, в случае
`LinkedHashSet` элементы добавляются в конец. А в случае с `HashSet` неизвестно куда. Да и толку нам от гарантированного
добавления в конец, если нам нужно заменить элементы в середине коллекции.

Таким образом, без складирования результатов в новую коллекцию не обойтись. А это означает, что требуется именно
функция.

## Уточненное условие

Итак, требуется написать обобщённую функцию, которая принимает в качестве аргумента коллекцию объектов, обходит её и
складывает элементы в список таким образом, что:

1. Если в исходной коллекции встречается только один объект, добавить его в выходной список.
2. Если в исходной коллекции встречаются подряд два одинаковых объекта и более, добавить в выходной список лишь одно
   значение `null`.
3. Функция должна быть рекурсивной.

Кстати, по отношению к множествам задача вообще не имеет смысла. Так как во множестве не может быть дубликатов.
Множество в качестве исходной коллекции - это вырожденный случай.

## Цикл

Решение с циклом проще, чем с рекурсией. Рассмотрим его сначала.

```java
import java.util.*;

import static java.util.Collections.*;

public class LoopSolution {
    public static <T> List<T> cleanCollection(Collection<T> input) {
        if (input.size() < 2)
            return unmodifiableList(new ArrayList<T>(input));

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

Запоминаем первый элемент. Затем идём по коллекции до тех пор, пока не встретится новое значение. Заодно подсчитываем
число дубликатов. После чего становится ясно, нужно ли объединять одинаковые элементы в `null`
или нет. Затем берём текущий элемент в качестве первого и мотаем цикл дальше.

Когда цикл завершается, за концом коллекции ничего не остаётся. И смена значений не происходит. Этот случай обрабатываем
отдельно.

## Рекурсия

Рекурсивное решение очень похоже на циклическое. Просто к той части исходной коллекции, которая осталась после смены
значений, применяется та же функция ещё раз. Как и с циклом, последний «кусочек» обрабатывается отдельно.

```java
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import static java.util.Collections.*;

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
                T mergedItem = duplicatesCount == 0
                        ? previousItem
                        : null;

                List<T> remainingItems = cleanTail(currentItem, tail);

                List<T> output = new ArrayList<>();
                output.add(mergedItem);
                output.addAll(remainingItems);
                return unmodifiableList(output);
            }
        } while (tail.hasNext());

        return singletonList(null);
    }
}
```

## Заключение

Задача как задача. Больше алгоритмическая, нежели Java-специфическая. Не слишком простая, но и не сложная. Ещё раз
убеждаюсь в том, что правильно заданный вопрос – это половина ответа.

## PS

Если вдруг кому-то интересны тесты.

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

