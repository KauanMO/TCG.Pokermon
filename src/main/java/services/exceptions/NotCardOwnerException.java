package services.exceptions;

public class NotCardOwnerException extends RuntimeException {
    public NotCardOwnerException() {
        super("You are not the owner of that card");
    }
}
