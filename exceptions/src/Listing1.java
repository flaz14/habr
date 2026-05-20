public class Listing1 {
    public static void main(String[] args) {
        try {
            System.out.println("> TRY block");
            throw new RuntimeException("exception from TRY block");
        } catch (RuntimeException e) {
            System.out.println("> CATCH block");
            System.out.println("> " + e);
            throw new RuntimeException("exception from CATCH block");
        }
    }
}