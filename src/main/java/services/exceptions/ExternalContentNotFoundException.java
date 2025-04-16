package services.exceptions;

public class ExternalContentNotFoundException extends RuntimeException {
    public ExternalContentNotFoundException(String externalContent) {
        super(externalContent + " not found");
    }
}