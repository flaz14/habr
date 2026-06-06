import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class PureRecursiveFunctionalSolutionTest {
    public static <T> List<T> cleanCollection(Collection<T> input) {
        if (input.size() < 2)
            return new ArrayList<>(input);

        List<T> output = greedyIterativeClean(input);

        if (output.size() < input.size())
            return cleanCollection(output);

        return output;
    }

    private static <T> List<T> greedyIterativeClean(Collection<T> input) {
        List<T> output = new ArrayList<>();

        Iterator<T> tail = input.iterator();
        T previousItem = tail.next();
        int duplicatesCount = 0;

        do {
            T currentItem = tail.next();
            if (areEqual(previousItem, currentItem)) {
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

    private static <T> boolean areEqual(T first, T second) {
        if (first == null && second == null)
            return true;

        if (first == null)
            return false;

        return first.equals(second);
    }

    private static <T> T merged(int duplicatesCount, T previousItem) {
        return duplicatesCount == 0
                ? previousItem
                : null;
    }

    @ParameterizedTest
    @MethodSource("data")
    void test(List<Integer> expected, List<Integer> input) {
        Collection<Integer> actual = cleanCollection(input);
        assertEquals(expected, actual);
    }

    static Stream<Arguments> data() {
        return Stream.of(
                arguments(
                        emptyList(),
                        emptyList()),

                arguments(
                        singletonList(null),
                        singletonList(null)),

                arguments(
                        singletonList(null),
                        asList(null, null)),

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
                        singletonList(null),
                        asList(1, 1, 2, 2, 3, 3)),

                arguments(
                        asList(2, null, 3),
                        asList(2, 4, 4, 3)),

                arguments(
                        singletonList(null),
                        singletonList(null)),

                arguments(
                        singletonList(null),
                        asList(null, null)),

                arguments(
                        asList(1, null, 2),
                        asList(1, null, null, 2)),

                arguments(
                        asList("dog", null, "dog"),
                        asList("dog", "cat", "cat", "dog")),

                arguments(
                        asList(null, "cat"),
                        asList("dog", "dog", null, "cat"))
        );
    }
}
