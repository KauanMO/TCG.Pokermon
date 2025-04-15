package rest.dtos.card;

import models.Card;

import java.util.List;

public record OutCardDTO(
        Long id,
        String name,
        String externalCode,
        String rarity,
        String description,
        List<String> types,
        List<String> subTypes,
        String evolvesFrom,
        ExternalCardImagesDTO images,
        Double price
) {
    public OutCardDTO(ExternalCardDTO c) {
        this(
                null,
                c.name(),
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

    public OutCardDTO(Card c) {
        this(
                c.getId(),
                c.getName(),
                c.getExternalCode(),
                c.getRarity().name(),
                c.getDescripton(),
                c.getTypes()
                        .stream()
                        .map(cardType ->
                                cardType
                                        .getType()
                                        .name())
                        .toList(),
                c.getSubtypes().stream()
                        .map(cardType ->
                                cardType
                                        .getSubtype()
                                        .name())
                        .toList(),
                c.getEvolvesFrom(),
                new ExternalCardImagesDTO(c.getSmallImage(), c.getLargeImage()),
                c.getPrice()
        );
    }
}
