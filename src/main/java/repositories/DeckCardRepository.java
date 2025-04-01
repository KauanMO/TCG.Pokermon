package repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import models.DeckCard;

@ApplicationScoped
public class DeckCardRepository implements PanacheRepository<DeckCard> {
}
