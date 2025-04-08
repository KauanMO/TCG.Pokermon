package repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import models.Card;

import java.util.List;

@ApplicationScoped
public class CardRepository implements PanacheRepository<Card> {
    public List<Card> findByUserId(Long userId) {
        return this.list("user.id = ?1", userId);
    }
}