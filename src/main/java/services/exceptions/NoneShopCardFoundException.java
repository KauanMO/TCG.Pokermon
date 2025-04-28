package services.exceptions;

public class NoneShopCardFoundException extends RuntimeException {
    public NoneShopCardFoundException() {
        super("None shop card found");
    }
}
