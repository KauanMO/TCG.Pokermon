package services.exceptions;

public class UsernameAlreadyTakenException extends RuntimeException {
    public UsernameAlreadyTakenException(String username) {
        super("Nome de usuário " + username + " não está disponível");
    }
}
