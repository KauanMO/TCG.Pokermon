package services;

import enums.CardRarityEnum;
import enums.CardTypeEnum;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import models.Card;
import models.CardType;
import models.User;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import repositories.CardRepository;
import rest.clients.CardsRestClient;
import rest.dtos.card.ExternalCardDTO;
import rest.dtos.card.OpenedCardDTO;
import rest.dtos.external.ExternalCardResponseDTO;
import services.exceptions.UserNotFoundException;
import utils.CardRarityPicker;

import java.util.Objects;
import java.util.Random;
import java.util.Set;

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

    private final String defaultSelectFilds = "id,name,images,rarity,set,cardmarket,,subtypes,types";

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

        String queryRarity = "\"" + CardRarityPicker.pickRarity().name().replace("_", " ") + "\"";

        Integer externalResponseTotal = getExternalTotalCards(queryRarity);

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
                .get("rarity:" + queryRarity, "id", 1)
                .totalCount();
    }

    public OpenedCardDTO openCardSet(String externalSetId, Long userId) {
        User userFound = userService.findUserById(userId).orElseThrow(UserNotFoundException::new);

        cardSetService.verifyCardSet(userFound, externalSetId);

        ExternalCardDTO externalCard = getRandomCardSetCard(externalSetId);

        Card newCard = Card.builder()
                .name(externalCard.name())
                .price(externalCard.cardmarket().prices().averageSellPrice())
                .largeImage(externalCard.images().large())
                .smallImage(externalCard.images().small())
                .rarity(CardRarityEnum.valueOf(externalCard.rarity()))
                .user(userFound)
                .descripton(externalCard.flavorText())
                .externalCode(externalCard.id())
                .quality(
                        (float) Math.round(
                                Math.random() * 2
                        * 1000 / 1000.0)
                )
                .setName(externalCard.set().name())
                .setId(externalCard.set().id())
                .build();

        repository.persist(newCard);



        return null;
    }
}