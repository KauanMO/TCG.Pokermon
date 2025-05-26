package services.dtos;

import models.Card;

import java.util.List;

public record MyCardsDTO(List<Card> cards,
                         Integer totalCards) {
}