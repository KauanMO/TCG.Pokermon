package services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import models.Deck;
import models.User;
import repositories.DeckRepository;
import rest.dtos.deck.CreateDeckDTO;
import services.exceptions.DeckNotFoundException;
import services.exceptions.UserNotFoundException;

@ApplicationScoped
public class DeckService {
    @Inject
    private DeckRepository repository;

    @Inject
    private UserService userService;

    @Transactional
    public Deck createDeck(CreateDeckDTO dto, Long userId) {
        User userFound = userService.findUserById(userId).orElseThrow(UserNotFoundException::new);

        Deck newDeck = Deck.builder()
                .user(userFound)
                .postition(dto.position())
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

    public Deck findByIdUserId(Long id, Long userId) {
        return repository.findByIdAndUserId(id, userId).orElseThrow(
                () -> new DeckNotFoundException("Deck not found or is not your deck")
        );
    }

    public Deck findActiveUserDeck(Long userId) {
        return repository.find("active = true and user.id = ?1", userId).firstResult();
    }
}