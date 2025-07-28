package repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import models.Deck;
import rest.dtos.deck.DeckExtraInfoDTO;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class DeckRepository implements PanacheRepository<Deck> {
    public Optional<Deck> findByIdAndUserId(Long id, Long userId) {
        return Optional.of(this.find("id = ?1 and user.id = ?2", id, userId).firstResult());
    }

    public List<Deck> findByUserId(Long userId) {
        return this.list("user.id = ?1", userId);
    }
}