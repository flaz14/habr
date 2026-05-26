import java.util.*;

public class Listing5 {
    public static final Random RANDOM = new Random(42);
    public static final int BOUND = 100;

    public static void main(String[] args) {
        Set<Integer> hashSet = new HashSet<>();
        hashSet.add(41);
        hashSet.add(19);
        hashSet.add(43);
        hashSet.add(13);
        hashSet.add(7);

        var iterator1 = hashSet.iterator();
        var iterator2 = hashSet.iterator();

        iterator1.next();
        iterator2.next();
        iterator2.remove();
        iterator1.next();
    }
}

