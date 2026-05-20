import java.util.HashSet;
import java.util.Set;

public class Listing1a {
    public static void main(String[] args) {
        Set<String> hashSet = new HashSet<>();
        hashSet.add("Апельсин");
        hashSet.add("Банан");
        hashSet.add("Лимон");

        hashSet.add("Абрикос");

        System.out.println(hashSet);
    }
}
