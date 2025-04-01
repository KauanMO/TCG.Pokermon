package rest.dtos.deckcard;

import java.util.List;

public record CreateDeckCardDTO(
        Long deckId,
        List<Long> cardIds
) { }