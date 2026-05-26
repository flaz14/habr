import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import static java.util.Collections.singletonList;

public class PureFunctionalSolution {
    public static <T> Collection<T> cleanCollection(Collection<T> collection) {
        if (collection.isEmpty())
            return collection;

        Iterator<T> tail = collection.iterator();

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

                Collection<T> otherItems = cleanTail(currentItem, tail);

                Collection<T> result = new ArrayList<>();
                result.add(mergedItem);
                result.addAll(otherItems);
                return result;
            }
        } while (tail.hasNext());

        return singletonList(null);
    }
}
