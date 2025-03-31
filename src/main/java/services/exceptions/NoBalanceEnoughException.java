package services.exceptions;

public class NoBalanceEnoughException extends RuntimeException {
    public NoBalanceEnoughException() {
        super("You dont have enough balance to but that card set");
    }
}
