package rest.dtos.deck;

import models.Deck;

public record OutDeckDTO(
        Long id,
        String name,
        Integer position,
        Boolean active
) {
    public OutDeckDTO(Deck d) {
        this(d.getId(), d.getName(), d.getPostition(), d.getActive());
    }
}