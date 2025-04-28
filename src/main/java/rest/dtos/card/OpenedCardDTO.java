package rest.dtos.card;

import models.Card;

public record OpenedCardDTO(
        String name,
        String externalCode,
        String largeImage
) {
    public OpenedCardDTO(Card c) {
        this(c.getShopCard().getName(), c.getShopCard().getExternalCode(), c.getShopCard().getLargeImage());
    }
}
