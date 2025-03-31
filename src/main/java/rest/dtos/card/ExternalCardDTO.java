package rest.dtos.card;

import rest.dtos.set.ExternalSetDTO;

import java.util.List;

public record ExternalCardDTO(
    String id,
    String name,
    String rarity,
    String flavorText,
    List<String> types,
    List<String> subtypes,
    ExternalCardImagesDTO images,
    ExternalSetDTO set,
    CardMarket cardmarket
) {
    public record CardMarket (Prices prices) { }

    public record Prices(Double averageSellPrice) { }
}