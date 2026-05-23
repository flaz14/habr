import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Set;

public class Listing3 {
    public static final Random RANDOM = new Random(42);
    public static final int BOUND = 100;

    public static void main(String[] args) {
        Set<Integer> linkedHashSet = new LinkedHashSet<>();
        for (int i = 0; i < 99; i++) {
            linkedHashSet.add(RANDOM.nextInt(BOUND));
            System.out.printf("%2d) %s\n", i + 1, linkedHashSet);
        }
    }
}

