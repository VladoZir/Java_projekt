package hr.java.waterUsageJavaFxApplication.exceptions;

public class NotNumberException extends Exception{
    public NotNumberException(String message) {
        super(message);
    }

    public NotNumberException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotNumberException(Throwable cause) {
        super(cause);
    }
}
