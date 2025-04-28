package rest.dtos.card;

import enums.CardSubtypeEnum;
import enums.CardTypeEnum;
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
                c.getShopCard().getName(),
                c.getShopCard().getExternalCode(),
                c.getShopCard().getRarity().name(),
                c.getShopCard().getDescripton(),
                c.getShopCard().getTypes().stream().map(CardTypeEnum::name).toList(),
                c.getShopCard().getSubtypes().stream().map(CardSubtypeEnum::name).toList(),
                c.getShopCard().getEvolvesFrom(),
                new ExternalCardImagesDTO(c.getShopCard().getSmallImage(), c.getShopCard().getLargeImage()),
                c.getPrice()
        );
    }
}
