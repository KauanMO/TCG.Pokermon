package rest.dtos.card;

import models.Card;

public record OpenedCardDTO(
        String name,
        String externalCode,
        String largeImage
) {
    public OpenedCardDTO(Card c) {
        this(c.getName(), c.getExternalCode(), c.getLargeImage());
    }
}
