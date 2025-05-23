package rest.dtos.user;

import models.User;

public record OutUserDTO(
        Long id,
        String username,
        String email,
        String token,
        Integer favoritePokemonCode
) {
    public OutUserDTO(User u) {
        this(u.getId(), u.getUsername(), u.getEmail(), null, u.getFavoritePokemonCode());
    }

    public OutUserDTO(User u, String token) {
        this(u.getId(), u.getUsername(), u.getEmail(), token, u.getFavoritePokemonCode());
    }
}