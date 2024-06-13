package hr.java.waterUsage.exceptions;

public class NumberTooLowException extends Exception{
    public NumberTooLowException(String message) {
        super(message);
    }

    public NumberTooLowException(String message, Throwable cause) {
        super(message, cause);
    }

    public NumberTooLowException(Throwable cause) {
        super(cause);
    }
}
