import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class Listing6 {
    public static final Random RANDOM = new Random(42);
    public static final int BOUND = 100;

    public static void main(String[] args) {
        Set<Integer> hashSet = new HashSet<>();
        hashSet.add(41);
        hashSet.add(null);
        hashSet.add(43);
        hashSet.add(null);


        System.out.println(hashSet);


    }
}

