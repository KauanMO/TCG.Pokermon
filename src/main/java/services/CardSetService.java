package services;

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
import rest.dtos.set.ExternalSetDTO;
import services.exceptions.CardSetNotFound;
import services.exceptions.NoBalanceEnoughException;
import services.exceptions.UserNotFoundException;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationScoped
public class CardSetService {
    @Inject
    private CardSetRepository repository;
    @RestClient
    private SetsRestClient setsRestClient;
    @Inject
    private UserService userService;
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

    public CardSetWithCardsDTO findByIdWithCards(Long id, Integer page) {
        CardSet cardSet = findById(id);

        Set<ExternalCardDTO> setCards = cardsRestClient
                .get("set.id:" + cardSet.getExternalId() + " supertype:pokemon",
                        "id,name,rarity,flavorText,types,subtypes,evolvesFrom,images,set,cardmarket",
                        20,
                        page,
                        "-cardmarket.prices.averageSellPrice")
                .data();


        List<OutCardDTO> orderedCards = new ArrayList<>(setCards.stream()
                .sorted(Comparator
                        .comparingDouble(c -> c.cardmarket()
                                .prices()
                                .averageSellPrice()))
                .map(OutCardDTO::new)
                .toList());

        Collections.reverse(orderedCards);

        return new CardSetWithCardsDTO(cardSet, orderedCards);
    }

    public void verifyCardSet(User user, String externalSetId) {
        CardSet cardSetFound = repository.findByExternalId(externalSetId).orElseThrow(CardSetNotFound::new);

        if (user.getBalance() < cardSetFound.getPrice()) throw new NoBalanceEnoughException();

        userService.updateUserBalance(user, user.getBalance() - cardSetFound.getPrice());
    }
}