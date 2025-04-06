package rest.dtos.card;

import java.util.List;

public record OutCardDTO(
        String name,
        String externalCode,
        String rarity,
        String description,
        List<String> types,
        List<String> subTypes,
        String evolvesFrom,
        ExternalCardImagesDTO images,
        Double averagePrice
) {
    public OutCardDTO(ExternalCardDTO c) {
        this(c.name(),
                c.id(),
                c.rarity(),
                c.flavorText(),
                c.types(),
                c.subtypes(),
                c.evolvesFrom(),
                c.images(),
                c.cardmarket()
                        .prices()
                        .averageSellPrice());
    }
}
