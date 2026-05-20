public class Listing4 {
    public static void main(String[] args) {
        try {
            System.out.println("> TRY block");
            throw new RuntimeException("exception from TRY block");
        } catch (RuntimeException primaryException) {
            try {
                System.out.println("> CATCH block");
                System.out.println("> " + primaryException);
                throw new RuntimeException("exception from CATCH block");
            } catch (RuntimeException secondaryException) {
                var joinedException = new RuntimeException("Joined exception");
                joinedException.addSuppressed(primaryException);
                joinedException.addSuppressed(secondaryException);
                throw joinedException;
            }
        }
    }
}

