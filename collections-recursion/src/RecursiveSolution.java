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
