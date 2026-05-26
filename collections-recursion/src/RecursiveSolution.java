import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import static java.util.Collections.singletonList;

public class RecursiveSolution {
    public static <T> Collection<T> cleanCollection(Collection<T> collection) {
        // If the collection is empty, there is no need to process it.
        if (collection.isEmpty())
            return collection;

        // The method should work on every kind of collection.
        // There is why we use iterator.
        // "Tail" points to the rest of the collection, e.g. unprocessed items.
        Iterator<T> tail = collection.iterator();

        // Call actual method since we would not like to bother a user with the iterator.
        return cleanTail(
                tail.next(),
                tail);
    }

    // This method does not modify original collection but returns new one. New collection contains unique items and
    // single null value for each group of same items. Therefore, this method is pure function.
    //
    // Replacement of the duplicated items "in place" is possible in general (even while iterators used). But  that
    // makes nonsense in case of recursion. If you would like to replace items "in place" please iterate over the
    // collection with aid of one more loop.
    //
    // However, recursive solution with modification of the input collection in "place" is still possible. But it's
    // complicated even more complicated than provided below.
    private static <T> Collection<T> cleanTail(T previousItem,
                                               Iterator<T> tail) {

        // No more items are left. Therefore, we just return previous one.
        if (!tail.hasNext())
            return singletonList(previousItem);

        // We should count how many items in a sequence are the same.
        int duplicatesCount = 0;
        do {
            T currentItem = tail.next();
            if (currentItem.equals(previousItem)) {
                // We encountered the same item, just increase the counter.
                duplicatesCount++;
            } else {
                // We encountered a different item. Let's analyze the counter.
                T mergedItem = duplicatesCount == 0
                        ? previousItem // transition from single item to another one, for example, [2, 1] -> [2, 1]
                        : null; // merge several equal item into null, for example, [2, 2, 1] -> [null, 1]

                // Apply the same logic for the rest of the collection.
                Collection<T> otherItems = cleanTail(currentItem, tail);

                // Put all together and exit from the loop.
                Collection<T> result = new ArrayList<>();
                result.add(mergedItem);
                result.addAll(otherItems);
                return result;
            }
        } while (tail.hasNext());

        // There are only duplicates, for example, [2, 2, 2] -> [null]
        return singletonList(null);
    }
}
