package rest.dtos.deckcard;

import java.util.List;

public record ValidatedCardsDTO(
        List<Long> validCardsIds,
        List<Long> invalidCardsIds
) {
}
