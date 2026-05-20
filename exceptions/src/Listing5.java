public class Listing5 {
    public static void main(String[] args) {
        try (FinallyBlock f = new FinallyBlock()) {
            System.out.println("> Processing...");
            // делаем ещё работу...
        } finally {
            // намеренно оставляем этот блок пустым
        }
    }
}

class FinallyBlock implements AutoCloseable {
    @Override
    public void close() {
        String message = "> Completed";
        System.out.println(message);
        // делаем c message что-то ещё...
    }
}
