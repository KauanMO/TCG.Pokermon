package repositories;

import enums.CardRarityEnum;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import models.ShopCard;

import java.util.List;

@ApplicationScoped
public class ShopCardRepository implements PanacheRepository<ShopCard> {
    public List<ShopCard> findBySetId(Long cardSetId) {
        return this.list("cardSet.id = ?1", cardSetId);
    }

    public List<ShopCard> findBySetIdAndRarity(Long cardSetId, CardRarityEnum rarity) {
        return this.list("cardSet.id = ?1 and rarity = ?2", cardSetId, rarity);
    }
}
