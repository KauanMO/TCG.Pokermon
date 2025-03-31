package services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import models.CardSet;
import models.User;
import repositories.CardSetRepository;
import services.exceptions.CardSetNotFound;
import services.exceptions.NoBalanceEnoughException;
import services.exceptions.UserNotFoundException;

import java.util.Set;
import java.util.stream.Collectors;

@ApplicationScoped
public class CardSetService {
    @Inject
    private CardSetRepository repository;

    @Inject
    private UserService userService;

    public Set<CardSet> findCardSets() {
        return repository
                .findAll()
                .stream()
                .collect(Collectors.toSet());
    }

    public void verifyCardSet(User user, String externalSetId) {
        CardSet cardSetFound = repository.findByExternalId(externalSetId).orElseThrow(CardSetNotFound::new);

        if (user.getBalance() < cardSetFound.getPrice()) throw new NoBalanceEnoughException();

        userService.updateUserBalance(user, user.getBalance() - cardSetFound.getPrice());
    }
}
