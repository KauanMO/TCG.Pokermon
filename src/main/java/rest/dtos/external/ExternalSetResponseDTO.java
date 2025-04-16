package rest.dtos.external;

import rest.dtos.cardSet.ExternalSetDTO;

import java.util.Set;

public record ExternalSetResponseDTO(
        Set<ExternalSetDTO> data,
        Integer page,
        Integer pageSize,
        Integer count,
        Integer totalCount
) {
}
