package websocket.dto;

import models.Card;

public record OutCardDTO(
        Long id,
        String name,
        String image,
        Double price,
        Double quality
) {
    public OutCardDTO(Card c) {
        this(c.getId(), c.getShopCard().getName(), c.getShopCard().getLargeImage(), c.getPrice(), c.getQuality());
    }
}
