package rest.dtos.external;

import rest.dtos.card.ExternalCardDTO;

import java.util.Set;

public record ExternalCardResponseDTO(
        Set<ExternalCardDTO> data,
        Integer page,
        Integer pageSize,
        Integer count,
        Integer totalCount
) {
}
