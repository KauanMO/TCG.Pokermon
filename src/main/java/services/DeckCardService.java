package services;

import enums.CardRarityEnum;
import enums.CardSubtypeEnum;
import enums.CardTypeEnum;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import models.Card;
import models.CardSubtype;
import models.Deck;
import models.DeckCard;
import repositories.DeckCardRepository;
import rest.dtos.deckcard.CreateDeckCardDTO;
import rest.dtos.deckcard.ValidatedCardsDTO;
import services.exceptions.CardNotFoundException;
import services.exceptions.DeckCardValidationException;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationScoped
public class DeckCardService {
    @Inject
    private DeckCardRepository repository;
    @Inject
    private CardService cardService;
    @Inject
    private DeckService deckService;
    @Inject
    private TokenService tokenService;

    @Transactional
    public ValidatedCardsDTO createDeckCards(CreateDeckCardDTO dto) {
        ValidatedCardsDTO validatedCards = new ValidatedCardsDTO(new ArrayList<>(), new ArrayList<>());
        List<DeckCard> newDeckCards = new ArrayList<>();

        Deck deckFound = deckService.findByIdUserId(dto.deckId(), tokenService.getUserId());

        List<Card> cards = validateNewDeckCards(deckFound, dto.cardIds());

        for (Card card : cards) {
            try {
                newDeckCards.add(
                        DeckCard.builder()
                                .card(card)
                                .deck(deckFound)
                                .build()
                );

                validatedCards
                        .validCardsIds()
                        .add(card.getId());
            } catch (CardNotFoundException e) {
                validatedCards
                        .invalidCardsIds()
                        .add(card.getId());
            }
        }

        if (!newDeckCards.isEmpty()) repository.persist(newDeckCards);

        return validatedCards;
    }

    @Transactional
    public void deleteDeckCardsByCardId(Long cardId) {
        repository.deleteByCardId(cardId);
    }

    @Transactional
    public void deleteDeckCardsByCardsIds(List<Long> cardsIds) {
        repository.deleteByCardsIds(cardsIds);
    }

    private List<Card> validateNewDeckCards(Deck deck, List<Long> cardsIds) {
        List<Card> cards = cardService.findCardsByIds(cardsIds);
        List<Card> newDeckCards = new ArrayList<>(cards);
        newDeckCards.addAll(deck.getCards().stream().map(DeckCard::getCard).toList());

        if (cards.isEmpty()) throw new DeckCardValidationException("None cards found");

        validateDeckSizeLimit(newDeckCards.size());
        validateDoubleCards(newDeckCards);
        validateSameSingleTypeCards(newDeckCards);
        validateMultipleTypesCards(newDeckCards);
        validateSameCardsSubtype(newDeckCards);
        validateLegendRarity(newDeckCards);
        validateSameRarityCards(newDeckCards);

        return cards;
    }

    private void validateDeckSizeLimit(Integer size) {
        if (size > 28) throw new DeckCardValidationException("Your deck can't have more than 28 cards");

        if(size < 20) throw new DeckCardValidationException("Your deck can't have less than 20 cards");
    }

    private void validateDoubleCards(List<Card> cards) {
        Set<String> seenExternalIds = new HashSet<>();

        Optional<Card> doubleCard = cards.stream()
                .filter(c -> !seenExternalIds.add(c.getShopCard().getExternalCode()))
                .findFirst();

        if (doubleCard.isPresent())
            throw new DeckCardValidationException("Duplicated card with external id: " + doubleCard.get().getShopCard().getExternalCode());
    }

    private void validateSameSingleTypeCards(List<Card> cards) {
        Map<CardTypeEnum, List<Card>> cardsByType = cards.stream()
                .filter(c -> c.getShopCard().getTypes().size() == 1)
                .collect(Collectors.groupingBy(c -> c.getShopCard().getTypes().getFirst()));

        for (Map.Entry<CardTypeEnum, List<Card>> entry : cardsByType.entrySet()) {
            if (entry.getValue().size() > 4) {
                throw new DeckCardValidationException("More than 4 cards from the type: " + entry.getKey().name());
            }
        }
    }

    private void validateMultipleTypesCards(List<Card> cards) {
        if (cards.stream()
                .filter(c -> c.getShopCard().getTypes().size() > 1)
                .toList()
                .size() > 8)
            throw new DeckCardValidationException("More than 8 cards with multiple types");
    }

    private void validateSameCardsSubtype(List<Card> cards) {
//        Map<CardSubtypeEnum, Long> typeCounts = cards.stream()
//                .flatMap(card -> card.getShopCard().getSubtypes().stream())
//                .collect(Collectors.groupingBy(CardSubtype::getSubtype, Collectors.counting()));
//
//        typeCounts.forEach((type, count) -> {
//            if (count > 4) {
//                throw new DeckCardValidationException("More than 4 cards from the subtype: " + type.name());
//            }
//        });
    }

    private void validateLegendRarity(List<Card> cards) {
        if (cards.stream()
                .filter(c -> c.getShopCard().getRarity().equals(CardRarityEnum.LEGEND))
                .toList()
                .size() > 3)
            throw new DeckCardValidationException("More than 3 legend rarity cards");
    }

    private void validateSameRarityCards(List<Card> cards) {
        Map<CardRarityEnum, List<Card>> cardsByRarity = cards.stream()
                .collect(Collectors.groupingBy(c -> c.getShopCard().getRarity()));

        for (CardRarityEnum rarity : cardsByRarity.keySet()) {
            if (cardsByRarity.get(rarity).size() > 8)
                throw new DeckCardValidationException("More than 8 cards of the rarity: " + rarity.name());
        }
    }
}