package hr.java.waterUsage.exceptions;

public class DuplicateTenantException extends RuntimeException{
    public DuplicateTenantException(String message) {
        super(message);
    }

    public DuplicateTenantException(String message, Throwable cause) {
        super(message, cause);
    }

    public DuplicateTenantException(Throwable cause) {
        super(cause);
    }
}
