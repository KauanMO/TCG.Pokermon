package services;

import enums.CardRarityEnum;
import enums.CardSubtypeEnum;
import enums.CardTypeEnum;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import models.CardSet;
import models.ShopCard;
import repositories.ShopCardRepository;
import rest.dtos.card.ExternalCardDTO;
import services.exceptions.NoneShopCardFoundException;
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
            shopCards.add(ShopCard.builder()
                    .descripton(card.flavorText())
                    .evolvesFrom(card.evolvesFrom())
                    .largeImage(card.images().large())
                    .smallImage(card.images().small())
                    .externalCode(card.id())
                    .rarity(CardRarityEnum.valueOf(StringHelper.enumStringBuilder(card.rarity())))
                    .averagePrice(card.cardmarket().prices().averageSellPrice())
                    .name(card.name())
                    .cardSet(cardSet)
                    .types(card
                            .types().stream()
                            .map(c -> CardTypeEnum.valueOf(StringHelper.enumStringBuilder(c)))
                            .toList())
                    .subtypes(card
                            .subtypes().stream()
                            .map(c -> CardSubtypeEnum.valueOf(StringHelper.enumStringBuilder(c)))
                            .toList())
                    .build());
        }

        repository.persist(shopCards);
    }

    public List<ShopCard> getByCardSetId(Long cardSetId) {
        return repository.findBySetId(cardSetId);
    }

    public ShopCard getRandomShopCardAndRarity(Long cardSetId) {
        CardRarityEnum rarity = CardRarityPicker.pickRarity();

        List<ShopCard> possibleCards = repository.findBySetIdAndRarity(cardSetId, rarity);

        if (possibleCards.isEmpty()) return getRandomShopCardAndRarity(cardSetId);

        Random random = new Random();

        return possibleCards.get(random.nextInt(possibleCards.size()));
    }
}