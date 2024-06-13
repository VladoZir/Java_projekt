package hr.java.waterUsage.exceptions;

public class LeakException extends RuntimeException{

    public LeakException(String message) {
        super(message);
    }

    public LeakException(String message, Throwable cause) {
        super(message, cause);
    }

    public LeakException(Throwable cause) {
        super(cause);
    }
}
