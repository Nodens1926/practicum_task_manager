package exceptions;

import java.io.IOException;

public class ManagerIOException extends RuntimeException {
    public ManagerIOException(String message) {
        super(message);
    }

    public ManagerIOException(String message, IOException e) {
    }
}
