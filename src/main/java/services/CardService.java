package services;

import enums.CardRarityEnum;
import enums.CardSubtypeEnum;
import enums.CardTypeEnum;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import models.Card;
import models.CardSubtype;
import models.CardType;
import models.User;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import repositories.CardRepository;
import repositories.CardSubtypeRepository;
import repositories.CardTypeRepository;
import rest.clients.CardsRestClient;
import rest.dtos.card.ExternalCardDTO;
import rest.dtos.card.OpenedCardDTO;
import rest.dtos.external.ExternalCardResponseDTO;
import services.exceptions.CardNotFoundException;
import services.exceptions.UserNotFoundException;
import utils.CardRarityPicker;
import utils.StringHelper;

import java.util.*;

@ApplicationScoped
public class CardService {
    @RestClient
    private CardsRestClient cardsRestClient;

    @Inject
    private CardRepository repository;

    @Inject
    private CardSetService cardSetService;

    @Inject
    private UserService userService;

    @Inject
    private CardTypeRepository cardTypeRepository;

    @Inject
    private CardSubtypeRepository cardSubtypeRepository;

    private final String defaultSelectFilds = "id,name,images,rarity,set,cardmarket,subtypes,types,flavorText,evolvesFrom";

    public Set<ExternalCardDTO> getCardsByName(String name) {
        ExternalCardResponseDTO externalResponse = cardsRestClient.get("name:" + name, defaultSelectFilds);

        return externalResponse.data();
    }

    public ExternalCardDTO getRandomCard(String rarity) {
        Random random = new Random();

        String queryRarity = "\"" + Objects.requireNonNullElseGet(rarity,
                () -> CardRarityPicker.pickRarity().name()).replace("_", " ") + "\"";

        Integer externalResponseTotal = getExternalTotalCards(queryRarity);

        Integer randomCardPosition = random.nextInt(externalResponseTotal + 1);

        ExternalCardResponseDTO externalResponse = cardsRestClient.get("rarity:" + queryRarity + " supertype:pokemon",
                defaultSelectFilds,
                1,
                randomCardPosition);

        return externalResponse
                .data()
                .iterator()
                .next();
    }

    private ExternalCardDTO getRandomCardSetCard(String externalSetId) {
        Random random = new Random();

        String queryRarity = StringHelper.generateRarityToQuery();

        Integer externalResponseTotal = getExternalTotalCards(queryRarity, externalSetId);

        if (externalResponseTotal < 1) return getRandomCardSetCard(externalSetId);

        Integer randomCardPosition = random.nextInt(externalResponseTotal + 1);

        ExternalCardResponseDTO externalResponse = cardsRestClient.get("rarity:" + queryRarity + " supertype:pokemon" + " set.id:" + externalSetId,
                defaultSelectFilds,
                1,
                randomCardPosition);

        return externalResponse
                .data()
                .iterator()
                .next();
    }

    private Integer getExternalTotalCards(String queryRarity) {
        return cardsRestClient
                .get("rarity:" + queryRarity + " supertype:pokemon", "id", 1)
                .totalCount();
    }

    private Integer getExternalTotalCards(String queryRarity, String externalSetId) {
        return cardsRestClient
                .get("rarity:" + queryRarity + " set.id:" + externalSetId + " supertype:pokemon", "id", 1)
                .totalCount();
    }

    @Transactional
    public Card openCardSet(String externalSetId, Long userId) {
        User userFound = userService.findUserById(userId).orElseThrow(UserNotFoundException::new);

        cardSetService.verifyCardSet(userFound, externalSetId);

        ExternalCardDTO externalCard = getRandomCardSetCard(externalSetId);

        Card newCard = Card.builder()
                .name(externalCard.name())
                .price(externalCard.cardmarket().prices().averageSellPrice())
                .largeImage(externalCard.images().large())
                .smallImage(externalCard.images().small())
                .rarity(CardRarityEnum.valueOf(StringHelper.enumStringBuilder(externalCard.rarity())))
                .user(userFound)
                .descripton(externalCard.flavorText())
                .externalCode(externalCard.id())
                .quality(
                        Math.round((Math.random() * 2) * 1000.0) / 1000.0
                )
                .evolvesFrom(externalCard.evolvesFrom())
                .setName(externalCard.set().name())
                .setId(externalCard.set().id())
                .build();

        repository.persist(newCard);

        registerCardType(newCard, externalCard.types());
        registerCardSubtype(newCard, externalCard.subtypes());

        return newCard;
    }

    @Transactional
    public void registerCardType(Card card, List<String> types) {
        List<CardType> cardTypes = new ArrayList<>();

        for (String type : types) {
            cardTypes.add(
                    CardType.builder()
                            .type(CardTypeEnum.valueOf(StringHelper.enumStringBuilder(type)))
                            .card(card)
                            .build()
            );
        }

        cardTypeRepository.persist(cardTypes);
    }

    @Transactional
    public void registerCardSubtype(Card card, List<String> subtypes) {
        List<CardSubtype> cardSubtypes = new ArrayList<>();

        for (String type : subtypes) {
            cardSubtypes.add(
                    CardSubtype.builder()
                            .subtype(CardSubtypeEnum.valueOf(StringHelper.enumStringBuilder(type)))
                            .card(card)
                            .build()
            );
        }

        cardSubtypeRepository.persist(cardSubtypes);
    }

    public Card findCardById(Long id) {
        Card cardFound = repository.findById(id);

        if (cardFound == null) throw new CardNotFoundException(id);

        return cardFound;
    }

    public List<Card> findCardsByIds(List<Long> ids) {
        return repository.list("id in ?1", ids);
    }
}