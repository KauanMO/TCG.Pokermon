package websocket.exceptions;

public class BettedCardsValueNotEnoughException extends WebSocketException {
    public BettedCardsValueNotEnoughException(Double currentValue, Double requiredValue) {
        super("The value of the betted cards is not enough.\nCurrent value: " + currentValue
                + "\nRequired value: " + requiredValue);
    }
}