package websocket.dto;

import models.User;

public record ManagedUserDTO(
        Long id,
        String username,
        String connectionId
) {
    public ManagedUserDTO(User u, String connectionId) {
        this(u.getId(), u.getUsername(), connectionId);
    }
}
