package rest.dtos.set;

import models.CardSet;
import rest.dtos.card.OutCardDTO;

import java.util.List;

public record CardSetWithCardsDTO(
        CardSet cardSet,
        List<OutCardDTO> cards,
        Integer totalCount
) {
}
