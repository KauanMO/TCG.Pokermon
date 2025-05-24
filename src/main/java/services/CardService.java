package services;

import enums.CardTypeEnum;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import models.*;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import repositories.CardRepository;
import rest.clients.CardsRestClient;
import rest.dtos.card.ExternalCardDTO;
import rest.dtos.external.ExternalCardResponseDTO;
import services.exceptions.CardNotFoundException;
import services.exceptions.UserNotFoundException;
import utils.CardRarityPicker;

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
    private ShopCardService shopCardService;
    @Inject
    private TokenService tokenService;

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
    public List<Card> openCardSet(Long setId, Integer amount) {
        User userFound = userService.findUserById(tokenService.getUserId()).orElseThrow(UserNotFoundException::new);
        CardSet cardSet = cardSetService.checkUserBalanceAndCardSet(userFound, setId);

        List<Card> newCards = new ArrayList<>();

        for (int i = 0; i < amount; i++) {
            ShopCard shopCard = shopCardService.getRandomShopCardAndRarity(cardSet.getId());

            double quality = Math.round((Math.random() * 2) * 1000.0) / 1000.0;

            newCards.add(Card.builder()
                    .shopCard(shopCard)
                    .quality(quality)
                    .price(shopCard.getAveragePrice() * quality)
                    .user(userFound)
                    .build());
        }

        repository.persist(newCards);

        return newCards;
    }

    public List<Card> findByUserId(Long userId) {
        return repository.findByUserId(userId);
    }

    public List<Card> findByUserIdOrderByCardType(Long userId, String orderBy, Boolean asc, List<CardTypeEnum> cardTypes, Integer page, Integer pageSize) {
        return repository.findByUserIdOrderByCardType(userId, orderBy, asc, cardTypes, page, pageSize);
    }

    public List<Card> findByUserIdOrderBy(Long userId, String orderBy, Boolean asc, Integer page, Integer pageSize) {
        return repository.findByUserIdOrderBy(userId, orderBy, asc, page, pageSize);
    }

    public List<Card> findMyCards(String orderBy, Boolean asc, List<CardTypeEnum> cardTypes, Integer page, Integer pageSize) {
        if (orderBy != null) {
            if (cardTypes != null && !cardTypes.isEmpty())
                return this.findByUserIdOrderByCardType(tokenService.getUserId(), orderBy, asc, cardTypes, page, pageSize);

            return this.findByUserIdOrderBy(tokenService.getUserId(), orderBy, asc, page, pageSize);
        }

        return this.findByUserId(tokenService.getUserId());
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