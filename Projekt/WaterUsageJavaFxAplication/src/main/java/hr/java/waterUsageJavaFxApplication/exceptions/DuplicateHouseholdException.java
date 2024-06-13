package hr.java.waterUsageJavaFxApplication.exceptions;

public class DuplicateHouseholdException extends Exception{
    public DuplicateHouseholdException(String message) {
        super(message);
    }

    public DuplicateHouseholdException(String message, Throwable cause) {
        super(message, cause);
    }

    public DuplicateHouseholdException(Throwable cause) {
        super(cause);
    }
}
