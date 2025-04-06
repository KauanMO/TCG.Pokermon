package infra.redis.dto;

import rest.dtos.card.OutCardDTO;

import java.util.List;

public record CardsIncrementDTO(
        Integer page,
        List<OutCardDTO> cards
) {
}
