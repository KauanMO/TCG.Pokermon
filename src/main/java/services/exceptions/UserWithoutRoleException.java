package services.exceptions;

public class UserWithoutRoleException extends RuntimeException {
    public UserWithoutRoleException() {
        super("That user does not have a role in the system");
    }
}
