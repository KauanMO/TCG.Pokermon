package services;

import infra.redis.IncrementOutCardDTOService;
import infra.redis.dto.CardsIncrementDTO;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import models.*;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import repositories.CardSetRepository;
import repositories.CardSubtypeRepository;
import repositories.CardTypeRepository;
import rest.clients.CardsRestClient;
import rest.clients.SetsRestClient;
import rest.dtos.card.OutCardDTO;
import rest.dtos.cardSet.CardSetWithCardsDTO;
import rest.dtos.cardSet.CreateCardSetDTO;
import rest.dtos.cardSet.ExternalSetDTO;
import rest.dtos.external.ExternalCardResponseDTO;
import services.exceptions.CardSetNotFound;
import services.exceptions.DuplicatedUniqueEntityException;
import services.exceptions.ExternalContentNotFoundException;
import services.exceptions.NoBalanceEnoughException;

import java.util.*;
import java.util.stream.Collectors;

@ApplicationScoped
public class CardSetService {
    @Inject
    private CardSetRepository repository;
    @Inject
    private UserService userService;
    @Inject
    private IncrementOutCardDTOService incrementService;
    @Inject
    private CardTypeRepository cardTypeRepository;
    @Inject
    private CardSubtypeRepository cardSubtypeRepository;
    @Inject
    private ShopCardService shopCardService;
    @RestClient
    private SetsRestClient setsRestClient;
    @RestClient
    private CardsRestClient cardsRestClient;

    @Transactional
    public CardSet createCardSet(CreateCardSetDTO dto) {
        if (repository.findByExternalId(dto.externalId()).isPresent()) {
            throw new DuplicatedUniqueEntityException("Cardset");
        }

        ExternalSetDTO cardSetResponse = setsRestClient.get("id:" + dto.externalId(), "id,name,series,images")
                .data()
                .stream()
                .findFirst()
                .orElseThrow(() -> new ExternalContentNotFoundException("Cardset"));

        ExternalCardResponseDTO externalResponse = cardsRestClient.get("set.id:" + dto.externalId(),
                "id,name,rarity,flavorText,types,subtypes,evolvesFrom,images,cardmarket");

        List<OutCardDTO> orderedCards = orderCardsByPriceDesc(externalResponse);

        Double cardSetPrice = Math.round(
                (orderedCards.stream()
                        .limit(3)
                        .mapToDouble(OutCardDTO::price)
                        .sum() / orderedCards.size()
                ) * 100.0
        ) / 100.0;

        CardSet cardSet = CardSet
                .builder()
                .name(cardSetResponse.name())
                .logo(cardSetResponse.images().logo())
                .symbol(cardSetResponse.images().symbol())
                .series(cardSetResponse.series())
                .externalId(cardSetResponse.id())
                .firstCardImage(orderedCards.getFirst().images().small())
                .secondCardImage(orderedCards.get(1).images().small())
                .thirdCardImage(orderedCards.getLast().images().small())
                .price(cardSetPrice)
                .build();

        repository.persist(cardSet);
        shopCardService.registerShopCards(externalResponse.data(), cardSet);

        return cardSet;
    }

    public Set<CardSet> findCardSets() {
        return repository
                .findAll()
                .stream()
                .collect(Collectors.toSet());
    }

    public CardSet findById(Long id) {
        CardSet cardset = repository.findById(id);

        if (cardset == null) throw new CardSetNotFound();

        return cardset;
    }

    public CardSetWithCardsDTO findByIdWithCards(Long id, Integer page, Integer pageSize) {
        CardSet cardSet = findById(id);

        var cardsRedis = incrementService.get(cardSet.getExternalId() + "page-" + page);

        if (cardsRedis != null && cardsRedis.page().equals(page))
            return new CardSetWithCardsDTO(cardSet, cardsRedis.cards(), cardsRedis.totalCount());

        ExternalCardResponseDTO externalResponse = requestCardsOrderedByPrice(cardSet.getExternalId(), pageSize, page);

        List<OutCardDTO> orderedCards = orderCardsByPriceDesc(externalResponse);

        incrementService.set(cardSet.getExternalId() + "page-" + page, new CardsIncrementDTO(page, orderedCards, externalResponse.totalCount()));

        return new CardSetWithCardsDTO(cardSet, orderedCards, externalResponse.totalCount());
    }

    public ExternalCardResponseDTO requestCardsOrderedByPrice(String cardSetId, Integer pageSize, Integer page) {
        return cardsRestClient
                .get("set.id:" + cardSetId + " supertype:pokemon",
                        "id,name,rarity,flavorText,types,subtypes,evolvesFrom,images,set,cardmarket",
                        pageSize,
                        page,
                        "-cardmarket.prices.averageSellPrice");
    }

    public List<OutCardDTO> orderCardsByPriceDesc(ExternalCardResponseDTO cards) {
        List<OutCardDTO> orderedCards = new ArrayList<>(cards.data().stream()
                .sorted(Comparator
                        .comparingDouble(c -> c.cardmarket()
                                .prices()
                                .averageSellPrice()))
                .map(OutCardDTO::new)
                .toList());
        Collections.reverse(orderedCards);

        return orderedCards;
    }

    public CardSet verifyCardSet(User user, Long setId) {
        CardSet cardSetFound = repository.findById(setId);

        if (cardSetFound == null) throw new CardSetNotFound();

        if (user.getBalance() < cardSetFound.getPrice()) throw new NoBalanceEnoughException();

        userService.updateUserBalance(user, user.getBalance() - cardSetFound.getPrice());

        return cardSetFound;
    }
}