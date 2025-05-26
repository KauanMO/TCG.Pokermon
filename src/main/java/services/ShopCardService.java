package services;

import enums.CardRarityEnum;
import enums.CardSubtypeEnum;
import enums.CardTypeEnum;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import models.CardSet;
import models.ShopCard;
import repositories.ShopCardRepository;
import rest.dtos.card.ExternalCardDTO;
import services.exceptions.NoneShopCardFoundException;
import utils.CardHelper;
import utils.CardRarityPicker;
import utils.StringHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

@ApplicationScoped
public class ShopCardService {
    @Inject
    private ShopCardRepository repository;

    @Transactional
    public void registerShopCards(Set<ExternalCardDTO> cards, CardSet cardSet) {
        List<ShopCard> shopCards = new ArrayList<>();

        for (ExternalCardDTO card : cards) {
            ShopCard shopCard = ShopCard.builder()
                    .descripton(card.flavorText())
                    .evolvesFrom(card.evolvesFrom())
                    .largeImage(card.images().large())
                    .smallImage(card.images().small())
                    .externalCode(card.id())
                    .rarity(CardRarityEnum.valueOf(StringHelper.enumStringBuilder(card.rarity())))
                    .averagePrice(CardHelper.getCardPrice(card))
                    .name(card.name())
                    .cardSet(cardSet)
                    .build();

            shopCard.getTypes().addAll(card
                    .types().stream()
                    .map(c -> CardTypeEnum.valueOf(StringHelper.enumStringBuilder(c)))
                    .toList());

            shopCard.getSubtypes().addAll(card
                    .subtypes().stream()
                    .map(c -> CardSubtypeEnum.valueOf(StringHelper.enumStringBuilder(c)))
                    .toList());

            shopCards.add(shopCard);
        }

        repository.persist(shopCards);
    }

    public PanacheQuery<ShopCard> getByCardSetIdOrderByAveragePrice(Long cardSetId, Integer page, Integer pageSize) {
        return repository.findBySetIdOrderByAveragePrice(cardSetId, page, pageSize);
    }

    public ShopCard getRandomShopCardAndRarity(Long cardSetId) {
        CardRarityEnum rarity = CardRarityPicker.pickRarity();

        List<ShopCard> possibleCards = repository.findBySetIdAndRarity(cardSetId, rarity);

        if (possibleCards.isEmpty()) return getRandomShopCardAndRarity(cardSetId);

        Random random = new Random();

        return possibleCards.get(random.nextInt(possibleCards.size()));
    }
}