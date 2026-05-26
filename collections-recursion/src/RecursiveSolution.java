import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import static java.util.Collections.singletonList;

public class RecursiveSolution {
    public static <T> Collection<T> cleanCollection(Collection<T> input) {
        if (input.isEmpty())
            return input;

        Iterator<T> tail = input.iterator();

        return cleanTail(
                tail.next(),
                tail);
    }

    private static <T> Collection<T> cleanTail(T previousItem,
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

                Collection<T> remainingItems = cleanTail(currentItem, tail);

                Collection<T> output = new ArrayList<>();
                output.add(mergedItem);
                output.addAll(remainingItems);
                return output;
            }
        } while (tail.hasNext());

        return singletonList(null);
    }
}
