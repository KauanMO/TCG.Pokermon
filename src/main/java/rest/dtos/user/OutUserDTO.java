package rest.dtos.user;

import models.User;

public record OutUserDTO(
        String username,
        String email
) {
    public OutUserDTO(User u) {
        this(u.getUsername(), u.getEmail());
    }
}