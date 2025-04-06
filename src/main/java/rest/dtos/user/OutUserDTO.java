package rest.dtos.user;

import models.User;

public record OutUserDTO(
        Long id,
        String username,
        String email
) {
    public OutUserDTO(User u) {
        this(u.getId(), u.getUsername(), u.getEmail());
    }
}