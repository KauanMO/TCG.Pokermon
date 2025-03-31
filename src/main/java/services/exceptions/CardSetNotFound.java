package services.exceptions;

public class CardSetNotFound extends RuntimeException {
    public CardSetNotFound() {
        super("Card set not found");
    }
}
