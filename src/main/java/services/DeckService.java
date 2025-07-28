package services;

import enums.CardSubtypeEnum;
import enums.CardTypeEnum;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import models.*;
import repositories.DeckRepository;
import rest.dtos.card.OutCardDTO;
import rest.dtos.deck.CreateDeckDTO;
import rest.dtos.deck.DeckExtraInfoDTO;
import rest.dtos.deck.OutDeckDTO;
import services.exceptions.CardNotFoundException;
import services.exceptions.DeckNotFoundException;
import services.exceptions.UserNotFoundException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ApplicationScoped
public class DeckService {
    @Inject
    private DeckRepository repository;
    @Inject
    private UserService userService;
    @Inject
    private TokenService tokenService;

    @Transactional
    public Deck createDeck(CreateDeckDTO dto) {
        User userFound = userService.findUserById(tokenService.getUserId()).orElseThrow(UserNotFoundException::new);

        Deck newDeck = Deck.builder()
                .user(userFound)
                .name(dto.name())
                .build();

        repository.persist(newDeck);

        return newDeck;
    }

    public Deck findById(Long id) {
        Deck deckFound = repository.findById(id);

        if (deckFound == null) throw new DeckNotFoundException(id);

        return deckFound;
    }

    public List<DeckExtraInfoDTO> findByUserId(Long userId) {
        Long finalUserId = userId == null
                ? tokenService.getUserId()
                : userId;

        return findExtraInfoByUserId(finalUserId);
    }

    public List<DeckExtraInfoDTO> findExtraInfoByUserId(Long userId) {
        List<Deck> decks = repository.findByUserId(userId);

        List<DeckExtraInfoDTO> decksInfo = new ArrayList<>();

        for (Deck deck : decks) {
            Map<CardTypeEnum, Long> typesCount = deck.getCards().stream()
                    .flatMap(dc -> dc.getCard().getShopCard().getTypes().stream())
                    .collect(Collectors.groupingBy(e -> e, Collectors.counting()));

            long maxTypeCount = typesCount.values().stream().max(Long::compare).orElse(0L);

            List<String> mainTypes = typesCount.entrySet().stream()
                    .filter(e -> e.getValue() == maxTypeCount)
                    .map(m -> m.getKey().name())
                    .toList();

            Card mainCard = deck.getCards().stream()
                    .max(Comparator.comparing(dc -> dc.getCard().getPrice()))
                    .get()
                    .getCard();

            decksInfo.add(new DeckExtraInfoDTO(new OutDeckDTO(deck), mainTypes, new OutCardDTO(mainCard)));
        }

        return decksInfo;
    }

    public Deck findByIdUserId(Long id, Long userId) {
        return repository.findByIdAndUserId(id, userId).orElseThrow(
                () -> new DeckNotFoundException("Deck not found or is not your deck")
        );
    }

    public Deck findActiveUserDeck(Long userId) {
        return repository.find("active = true and user.id = ?1", userId).firstResult();
    }
}