package services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import models.Deck;
import models.User;
import repositories.DeckRepository;
import rest.dtos.deck.CreateDeckDTO;
import services.exceptions.DeckNotFoundException;
import services.exceptions.UserNotFoundException;

import java.util.List;

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

    public List<Deck> findByUserId(Long userId) {
        Long finalUserId = userId == null
                ? tokenService.getUserId()
                : userId;

        return repository.findByUserId(finalUserId);
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