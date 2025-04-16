package services.exceptions;

public class DuplicatedUniqueEntityException extends RuntimeException {
    public DuplicatedUniqueEntityException(String entity) {
        super("Duplicated unique entity: " + entity);
    }
}