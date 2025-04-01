package services.exceptions;

public class DeckNotFoundException extends RuntimeException {
    public DeckNotFoundException(Long id) {
        super("Deck with id " + id + " not found");
    }

    public DeckNotFoundException(String message) {
        super(message);
    }
}
