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

    @ParameterizedTest
    @MethodSource("data")
    void recursiveProceduralSolutionTest(List<Integer> expected, List<Integer> input) {
        Collection<Integer> actual = RecursiveProceduralSolution.cleanCollection(input);
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