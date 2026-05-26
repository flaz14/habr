import java.util.*;

public class Remove {
    public static final Random RANDOM = new Random(42);
    public static final int BOUND = 200;
    public static final int COUNT = 49;

    public static void main(String[] args) {
        Set<Integer> hashSet = new HashSet<>();
        for (int i = 0; i < COUNT; i++) {
            hashSet.add(RANDOM.nextInt(BOUND));
        }

//        List<Integer> list = new ArrayList<>(
//                hashSet.
//
//        );

//        removeFromCollection(list);
        removeFromCollection(hashSet);
    }


    private static void removeFromCollection(Collection<Integer> collection) {
        System.out.printf("Remove from collection %s\n", collection.getClass().getCanonicalName());
        int i = 0;
        while (!collection.isEmpty()) {
            Iterator<Integer> iterator = collection.iterator();
            if (iterator.hasNext()) {
                iterator.next();
                iterator.remove();
                i++;
            }
            System.out.printf("%2d) %s\n", i + 1, collection);
        }
    }
}

