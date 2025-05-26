package rest.dtos.card;

import java.util.List;

public record MyCardsDTO(
        List<OutCardDTO> cards,
        Integer totalCards
) {
}
