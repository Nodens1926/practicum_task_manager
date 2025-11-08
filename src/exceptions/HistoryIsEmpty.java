package exceptions;

public class HistoryIsEmpty extends RuntimeException {
    public HistoryIsEmpty(String message) {
        super(message);
    }
}
