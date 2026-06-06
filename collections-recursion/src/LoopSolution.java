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
