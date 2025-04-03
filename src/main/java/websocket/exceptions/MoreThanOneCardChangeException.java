package websocket.exceptions;

public class MoreThanOneCardChangeException extends WebSocketException {
    public MoreThanOneCardChangeException() {
        super("You can just change one card once");
    }
}
