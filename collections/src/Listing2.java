import java.util.*;

public class Listing2 {
    public static final Random RANDOM = new Random(42);
    public static final int BOUND = 100;

    public static void main(String[] args) {
        Set<Integer> hashSet = new HashSet<>();
        for (int i = 0; i < 99; i++) {
            hashSet.add(RANDOM.nextInt(BOUND));
            System.out.printf("%2d) %s\n", i + 1, hashSet);
        }
    }
}


// new TreeSet<>(hashSet)
