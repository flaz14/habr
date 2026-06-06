public class Listing2 {
    public static void main(String[] args) throws Exception {
        try (AutoCloseable r = () -> {
            System.out.println("> TWR block");
            throw new RuntimeException("exception from TWR block");
        }) {
            System.out.println("> TRY block");
            throw new RuntimeException("exception from TRY block");
        }
    }
}
