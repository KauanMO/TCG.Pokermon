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
        this(c.getId(), c.getName(), c.getLargeImage(), c.getPrice(), c.getQuality());
    }
}
