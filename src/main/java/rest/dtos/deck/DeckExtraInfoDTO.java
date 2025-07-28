package rest.dtos.deck;

import models.Card;
import rest.dtos.card.OutCardDTO;

import java.util.List;

public record DeckExtraInfoDTO(
        OutDeckDTO deck,
        List<String> mainTypes,
        OutCardDTO mainCard
) {
}
