import java.util.HashSet;
import java.util.Set;

public class ConcurrentModificationExample {
    public static void main(String[] args) {
        Set<Integer> hashSet = new HashSet<>();
        hashSet.add(41);
        hashSet.add(19);
        hashSet.add(43);
        hashSet.add(13);

        var iterator1 = hashSet.iterator();
        var iterator2 = hashSet.iterator();

        iterator1.next();
        iterator2.next();
        iterator2.remove();
        iterator1.next();
    }
}

