package rest.dtos.card;

import enums.CardSubtypeEnum;
import enums.CardTypeEnum;
import models.Card;
import models.ShopCard;

import java.time.LocalDate;
import java.util.List;

public record OutCardDTO(
        Long id,
        LocalDate createdDate,
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
    public OutCardDTO(ExternalCardDTO c, Double price) {
        this(
                null,
                null,
                c.name(),
                c.id(),
                c.rarity(),
                c.flavorText(),
                c.types(),
                c.subtypes(),
                c.evolvesFrom(),
                c.images(),
                price);
    }

    public OutCardDTO(Card c) {
        this(
                c.getId(),
                c.getCreatedDate(),
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

    public OutCardDTO(ShopCard sc) {
        this(
                sc.getId(),
                null,
                sc.getName(),
                sc.getExternalCode(),
                sc.getRarity().name(),
                sc.getDescripton(),
                sc.getTypes().stream().map(CardTypeEnum::name).toList(),
                sc.getSubtypes().stream().map(CardSubtypeEnum::name).toList(),
                sc.getEvolvesFrom(),
                new ExternalCardImagesDTO(sc.getSmallImage(), sc.getLargeImage()),
                sc.getAveragePrice()
        );
    }
}
