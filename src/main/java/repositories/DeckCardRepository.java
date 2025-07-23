package repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import models.CardSet;
import models.DeckCard;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class DeckCardRepository implements PanacheRepository<DeckCard> {
    public void deleteByCardId(Long cardId) {
        this.delete("card.id = ?1", cardId);
    }

    public void deleteByCardsIds(List<Long> cardId) {
        this.delete("card.id in ?1", cardId);
    }
}
