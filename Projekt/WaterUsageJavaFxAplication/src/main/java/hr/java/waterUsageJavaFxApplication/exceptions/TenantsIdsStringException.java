package hr.java.waterUsageJavaFxApplication.exceptions;

public class TenantsIdsStringException extends RuntimeException{

        public TenantsIdsStringException(String message) {
            super(message);
        }

        public TenantsIdsStringException(String message, Throwable cause) {
            super(message, cause);
        }

        public TenantsIdsStringException(Throwable cause) {
            super(cause);
        }
}
