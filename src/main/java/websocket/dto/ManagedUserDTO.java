package websocket.dto;

import models.User;

public record ManagedUserDTO(
        Long id,
        String username
) {
    public ManagedUserDTO(User u) {
        this(u.getId(), u.getUsername());
    }
}
