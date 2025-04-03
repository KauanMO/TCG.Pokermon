package websocket.dto;

import models.Card;

import java.util.List;

public record BettedCardsDTO(List<Card> cards, ManagedUserDTO nextPlayer) {
}
