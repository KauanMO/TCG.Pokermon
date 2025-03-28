package rest.dtos.card;

import rest.dtos.set.ExternalSetDTO;

public record ExternalCardDTO(
    String id,
    String name,
    ExternalCardImagesDTO images,
    ExternalSetDTO set,
    String rarity
) {
}
