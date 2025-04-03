package websocket.exceptions;

public class WrongUserTurnException extends WebSocketException {
    public WrongUserTurnException() {
        super("Thats not your turn");
    }
}
