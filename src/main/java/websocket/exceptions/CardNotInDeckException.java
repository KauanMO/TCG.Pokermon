package websocket.exceptions;

public class CardNotInDeckException extends WebSocketException {
    public CardNotInDeckException(Long id) {
        super("The card with id: " + id + " is not in your deck");
    }
}
