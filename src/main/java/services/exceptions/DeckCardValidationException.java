package services.exceptions;

public class DeckCardValidationException extends RuntimeException {
    public DeckCardValidationException(String message) {
        super(message);
    }
}
