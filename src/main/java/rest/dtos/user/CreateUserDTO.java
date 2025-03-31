package rest.dtos.user;

public record CreateUserDTO(
        String username,
        String email,
        String password
) {
}
