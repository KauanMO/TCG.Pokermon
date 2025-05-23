package rest.dtos.user;

import models.User;

import java.time.LocalDate;

public record UserInfoDTO(
        String username,
        LocalDate createdDate,
        Double balance,
        Integer cardAmount,
        Integer deckAmount,
        Integer favoritePokemonCode
) {
    public UserInfoDTO(User u) {
        this(u.getUsername(), u.getCreatedDate(), u.getBalance(), u.getCards().size(), u.getDecks().size(), u.getFavoritePokemonCode());
    }
}
