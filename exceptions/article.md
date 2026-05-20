# Обработка исключений, возникших при обработке исключений

Исключения рождаются не только в основном коде, но и в обработчиках этих самых исключений. Зачастую вопросу не
уделяется должного внимания. Действительно, что может пойти не так в блоке `catch`? Там ведь код тривиальный! Но это
только на первый взгляд.

Например, безобидный `LOG.warn("...")` выливается в десяток вызовов нижележащих методов. И чем больше «наслоений» в
библиотеке логгирования, тем выше вероятность сбоя. Всё бы ничего, если бы не одна особенность языка Java...

## Суть проблемы

Исключение, возникшее в блоке `catch`, «проглатывает» исключение, возникшее в блоке `try`.

<anchor>listing-1</anchor>

### Листинг 1

```java
public class Listing1 {
    public static void main(String[] args) {
        try {
            System.out.println("> TRY block");
            throw new RuntimeException("exception from TRY block");
        } catch (Exception e) {
            System.out.println("> CATCH block");
            System.out.println("> " + e);
            throw new RuntimeException("exception from CATCH block");
        }
    }
}
```

Запустим код из [листинга 1](#listing-1). Как видим, исключение из `try` не попало в стектрейс, как будто его не было
вовсе:

```
> TRY block
> CATCH block
> java.lang.RuntimeException: exception from TRY block
Exception in thread "main" java.lang.RuntimeException: exception from CATCH block
	at Listing1.main(Listing1.java:9)
```

## Подавленный, но не раздавленный

Немного отвлечёмся и рассмотрим автоматическое закрытие ресурсов, а именно
[try-with-resources](https://dev.java/learn/exceptions/catching-handling/#try-with-resources). С ним ситуация несколько
иная. Если исключение возникло и в блоке `try`, и в блоке <abbr title="try-with-resources">TWR</abbr>, то второе не
пропадает бесследно, а добавляется к первому и становится
подавленным ([suppressed](https://dev.java/learn/exceptions/catching-handling/#suppressed)) по отношению к нему.

<anchor>listing-2</anchor>

### Листинг 2

```java
public class Listing1a {
    public static void main(String[] args) throws Exception {
        try (AutoCloseable r = () -> {
            System.out.println("> TWR block");
            throw new RuntimeException("exception from TWR block");
        }) {
            System.out.println("> TRY block");
            throw new RuntimeException("exception from TRY block");
        }
    }
}
```

Прогоним [листинг 2](#listing-2) и увидим, что оба исключения присутствуют в стектрейсе:

```
> TRY block
> TWR block
Exception in thread "main" java.lang.RuntimeException: exception from TRY block
	at Listing1a.main(Listing1a.java:8)
	Suppressed: java.lang.RuntimeException: exception from TWR block
		at Listing1a.lambda$main$0(Listing1a.java:5)
		at Listing1a.main(Listing1a.java:3)
```

Попробуем добиться такого же эффекта применительно к блоку `catch`.

## Решение

К сожалению, <abbr title="try-with-resources">TWR</abbr> здесь не поможет, ибо он выполняется **до** `catch`. Остаётся
лишь вложенный `try` и явное добавление вторичного исключения к первичному.

<anchor>listing-3</anchor>

### Листинг 3

```java
public class Listing3 {
    public static void main(String[] args) {
        try {
            System.out.println("> TRY block");
            throw new RuntimeException("exception from TRY block");
        } catch (RuntimeException primaryException) {
            try {
                System.out.println("> CATCH block");
                System.out.println("> " + primaryException);
                throw new RuntimeException("exception from CATCH block");
            } catch (RuntimeException secondaryException) {
                primaryException.addSuppressed(secondaryException);
                throw primaryException;
            }
        }
    }
}
```

Запустим [листинг 3](#listing-3) и убедимся, что исключение из `catch` не пропало:

```
> TRY block
> CATCH block
> java.lang.RuntimeException: exception from TRY block
Exception in thread "main" java.lang.RuntimeException: exception from TRY block
	at Listing3.main(Listing3.java:5)
	Suppressed: java.lang.RuntimeException: exception from CATCH block
		at Listing3.main(Listing3.java:10)
```

Стоит отметить, что метод
[Throwable.addSuppressed()](https://docs.oracle.com/en/java/javase/26/docs/api/java.base/java/lang/Throwable.html#addSuppressed(java.lang.Throwable))
добавит вторичное исключение только в том случае, если первичное было создано с параметром `enableSuppression`,
установленным в `true` (что делается по умолчанию).

А вот выяснить, было ли разрешено добавление исключений, затруднительно. Так,
метод [Throwable.getSuppressed()](https://docs.oracle.com/en/java/javase/26/docs/api/java.base/java/lang/Throwable.html#getSuppressed())
возвращает пустой массив, если подавленных исключений ещё нет **или** их добавление запрещено. Различить этих два случая
нельзя. Можно, конечно, измерить длину массива перед добавлением и после... А можно не мучаться, сразу создать ещё одно
исключение и прицепить к нему уже имеющиеся.

<anchor>listing-4</anchor>

### Листинг 4

```java
public class Listing4 {
    public static void main(String[] args) {
        try {
            System.out.println("> TRY block");
            throw new RuntimeException("exception from TRY block");
        } catch (RuntimeException primaryException) {
            try {
                System.out.println("> CATCH block");
                System.out.println("> " + primaryException);
                throw new RuntimeException("exception from CATCH block");
            } catch (RuntimeException secondaryException) {
                var joinedException = new RuntimeException("Joined exception");
                joinedException.addSuppressed(primaryException);
                joinedException.addSuppressed(secondaryException);
                throw joinedException;
            }
        }
    }
}
```

Результат работы [листинга 4](#listing-4) выглядит неплохо. Но что-то здесь не так: стектрейс начинается с исключения,
которое возникло в потоке выполнения последним. Такая перестановка может сбить с толку:

```
> TRY block
> CATCH block
> java.lang.RuntimeException: exception from TRY block
Exception in thread "main" java.lang.RuntimeException: Joined exception
	at Listing4.main(Listing4.java:12)
	Suppressed: java.lang.RuntimeException: exception from TRY block
		at Listing4.main(Listing4.java:5)
	Suppressed: java.lang.RuntimeException: exception from CATCH block
		at Listing4.main(Listing4.java:10)
```

В конце концов, если первичное исключение было создано с запретом на добавление подавленных исключений (опять-таки, по
умолчанию добавление разрешено), значит, на это была веская причина.

## Немного о finally

Порождённое в блоке `finally` исключение, в свою очередь, уничтожает исключения из всех вышестоящих блоков. Поэтому
лучше выполнять в `finally` действительно простые действия, а в остальных случаях использовать
<abbr title="try-with-resources">TWR</abbr>, даже если реальный ресурс (вроде файла) не задействован:

<anchor>listing-5</anchor>

### Листинг 5

```java
public class Listing5 {
    public static void main(String[] args) {
        try (FinallyBlock f = new FinallyBlock()) {
            System.out.println("> Processing...");
            // делаем ещё работу 
        } finally {
            // намеренно оставляем этот блок пустым
        }
    }
}

class FinallyBlock implements AutoCloseable {
    @Override
    public void close() {
        String message = "> Completed";
        System.out.println(message);
        // делаем c message что-то ещё...
    }
}
```

Блок `finally` проигрывает <abbr title="try-with-resources">TWR</abbr> во всём, кроме:

1. Выполняется после `catch`.
2. В нём можно присваивать значения переменным, объявленным выше `try`.

## Заключение

Работа над ошибками, порождёнными предыдущей работой над ошибками – не такая уж безумная затея. Но всего нужно в меру.
Как говорил один умный человек, не превращайте паранойю в маразм.

Что до языка Java, вышеописанные особенности не являются его изъянами. Обстоятельства могут сложиться так, что одно
исключение просто необходимо подменить другим. И лучше иметь такую возможность, чем не иметь.