package repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import models.Deck;

@ApplicationScoped
public class DeckRepository implements PanacheRepository<Deck> {
}