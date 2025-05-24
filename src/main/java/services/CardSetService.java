package services;

import infra.redis.IncrementOutCardDTOService;
import infra.redis.dto.CardsIncrementDTO;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
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
import rest.dtos.card.ExternalCardDTO;
import rest.dtos.card.OutCardDTO;
import rest.dtos.cardSet.CardSetWithCardsDTO;
import rest.dtos.cardSet.CreateCardSetDTO;
import rest.dtos.cardSet.ExternalSetDTO;
import rest.dtos.external.ExternalCardResponseDTO;
import services.exceptions.CardSetNotFoundException;
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

        ExternalCardResponseDTO externalResponse = cardsRestClient.get("supertype:pokemon set.id:" + dto.externalId(),
                "id,name,rarity,flavorText,types,subtypes,evolvesFrom,images,cardmarket");

        List<OutCardDTO> orderedCards = orderCardsByPriceDesc(externalResponse.data());

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

    public List<OutCardDTO> orderCardsByPriceDesc(Set<ExternalCardDTO> cards) {
        return cards.stream()
                .sorted(Comparator.comparingDouble((ExternalCardDTO c) -> c.cardmarket().prices().averageSellPrice())
                        .reversed())
                .map(OutCardDTO::new)
                .toList();
    }

    public Set<CardSet> findCardSets() {
        return repository
                .findAll()
                .stream()
                .collect(Collectors.toSet());
    }

    public CardSet findById(Long id) {
        CardSet cardset = repository.findById(id);

        if (cardset == null) throw new CardSetNotFoundException();

        return cardset;
    }

    public CardSetWithCardsDTO findByIdWithCards(Long id, Integer page, Integer pageSize) {
        CardSet cardSet = findById(id);

        PanacheQuery<ShopCard> cardsQuery = shopCardService.getByCardSetIdOrderByAveragePrice(id, page, pageSize);

        return new CardSetWithCardsDTO(cardSet,
                cardsQuery.stream()
                        .map(OutCardDTO::new)
                        .toList(),
                (int) cardsQuery.count());
    }

    public CardSet checkUserBalanceAndCardSet(User user, Long setId) {
        CardSet cardSetFound = repository.findById(setId);

        if (cardSetFound == null) throw new CardSetNotFoundException();

        if (user.getBalance() < cardSetFound.getPrice()) throw new NoBalanceEnoughException();

        userService.updateUserBalance(user, user.getBalance() - cardSetFound.getPrice());

        return cardSetFound;
    }
}