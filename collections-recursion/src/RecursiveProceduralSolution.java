import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.Collections.unmodifiableList;

public class RecursiveProceduralSolution {
    public static <T> List<T> cleanCollection(Collection<T> input) {
        if (input.isEmpty())
            return emptyList();
        Iterator<T> tail = input.iterator();
        List<T> output = new ArrayList<>(input.size());
        cleanTail(tail.next(), tail, output);
        return output;
    }

    private static <T> void cleanTail(T previousItem,
                                      Iterator<T> tail,
                                      List<T> output) {
        if (!tail.hasNext()) {
            output.add(previousItem);
            return;
        }
        int duplicatesCount = 0;
        do {
            T currentItem = tail.next();
            if (currentItem.equals(previousItem)) {
                duplicatesCount++;
            } else {
                T mergedItem = duplicatesCount == 0 ? previousItem : null;
                output.add(mergedItem);
                cleanTail(currentItem, tail, output);
                return;
            }
        } while (tail.hasNext());
        output.add(null);
    }
}
