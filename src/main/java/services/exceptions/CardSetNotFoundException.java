package services.exceptions;

public class CardSetNotFoundException extends RuntimeException {
    public CardSetNotFoundException() {
        super("Card set not found");
    }
}
