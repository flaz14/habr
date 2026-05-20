import java.util.HashMap;
import java.util.Map;

public class Listing1 {
    public static void main(String[] args) {
        Map<Integer, Integer> hashMap = new HashMap<>();

        hashMap.put(1, 1);
        hashMap.put(5, 2);
        hashMap.put(7, 3);
        hashMap.put(2, 4);

        hashMap.put(3, 1);
        hashMap.put(8, 2);
        hashMap.put(9, 3);
        hashMap.put(10, 4);

        hashMap.put(11, 1);
        hashMap.put(26, 2);
        hashMap.put(35, 3);
        hashMap.put(42, 4);

        hashMap.put(18, 1);
        hashMap.put(4, 2);
        hashMap.put(28, 3);
        hashMap.put(15, 4);

        System.out.println(hashMap);
    }
}
