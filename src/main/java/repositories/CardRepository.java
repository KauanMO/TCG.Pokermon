package repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import models.Card;

@ApplicationScoped
public class CardRepository implements PanacheRepository<Card> {
}
