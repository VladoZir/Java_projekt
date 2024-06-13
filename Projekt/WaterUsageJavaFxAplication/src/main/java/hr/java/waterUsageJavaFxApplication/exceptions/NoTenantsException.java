package hr.java.waterUsageJavaFxApplication.exceptions;

public class NoTenantsException extends Exception{
    public NoTenantsException(String message) {
        super(message);
    }

    public NoTenantsException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoTenantsException(Throwable cause) {
        super(cause);
    }
}
