import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class LoopSolution {
    public static <T> Collection<T> cleanCollection(Collection<T> input) {
        if (input.size() < 2)
            return input;

        Collection<T> output = new ArrayList<>();

        Iterator<T> tail = input.iterator();
        T previousItem = tail.next();
        int duplicatesCount = 0;
        do {
            T currentItem = tail.next();
            if (currentItem.equals(previousItem)) {
                duplicatesCount++;
            } else {
                T mergedItem = duplicatesCount == 0
                        ? previousItem
                        : null;
                output.add(mergedItem);
                previousItem = currentItem;
                duplicatesCount = 0;
            }
        } while (tail.hasNext());

        T lastItem = duplicatesCount == 0
                ? previousItem
                : null;

        output.add(lastItem);

        return output;
    }
}
