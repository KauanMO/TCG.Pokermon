package services;

import infra.redis.IncrementOutCardDTOService;
import infra.redis.dto.CardsIncrementDTO;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import models.CardSet;
import models.User;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import repositories.CardSetRepository;
import rest.clients.CardsRestClient;
import rest.clients.SetsRestClient;
import rest.dtos.card.ExternalCardDTO;
import rest.dtos.card.OutCardDTO;
import rest.dtos.set.CardSetWithCardsDTO;
import services.exceptions.CardSetNotFound;
import services.exceptions.NoBalanceEnoughException;

import java.util.*;
import java.util.stream.Collectors;

@ApplicationScoped
public class CardSetService {
    @Inject
    private CardSetRepository repository;
    @RestClient
    private SetsRestClient setsRestClient;
    @Inject
    private UserService userService;
    @Inject
    private IncrementOutCardDTOService incrementService;
    @RestClient
    private CardsRestClient cardsRestClient;

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

        var externalResponse = cardsRestClient
                .get("set.id:" + cardSet.getExternalId() + " supertype:pokemon",
                        "id,name,rarity,flavorText,types,subtypes,evolvesFrom,images,set,cardmarket",
                        pageSize,
                        page,
                        "-cardmarket.prices.averageSellPrice");

        List<OutCardDTO> orderedCards = new ArrayList<>(externalResponse.data().stream()
                .sorted(Comparator
                        .comparingDouble(c -> c.cardmarket()
                                .prices()
                                .averageSellPrice()))
                .map(OutCardDTO::new)
                .toList());
        Collections.reverse(orderedCards);

        incrementService.set(cardSet.getExternalId() + "page-" + page, new CardsIncrementDTO(page, orderedCards, externalResponse.totalCount()));

        return new CardSetWithCardsDTO(cardSet, orderedCards, externalResponse.totalCount());
    }

    public void verifyCardSet(User user, String externalSetId) {
        CardSet cardSetFound = repository.findByExternalId(externalSetId).orElseThrow(CardSetNotFound::new);

        if (user.getBalance() < cardSetFound.getPrice()) throw new NoBalanceEnoughException();

        userService.updateUserBalance(user, user.getBalance() - cardSetFound.getPrice());
    }
}