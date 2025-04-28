package repositories;

import enums.CardRarityEnum;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import models.ShopCard;

import java.util.List;

@ApplicationScoped
public class ShopCardRepository implements PanacheRepository<ShopCard> {
    public List<ShopCard> findBySetId(Long cardSetId) {
        return this.list("cardSet.id = ?1", cardSetId);
    }

    public List<ShopCard> findBySetId(Long cardSetId, Integer pageSize, Integer page) {
        return this.list("cardSet.id = ?1", cardSetId);
    }

    public PanacheQuery<ShopCard> findBySetIdOrderByAveragePrice(Long cardSetId, Integer page, Integer pageSize) {
        return this.find("cardSet.id = ?1 order by averagePrice desc", cardSetId)
                .page(Page.of(page, pageSize));
    }

    public List<ShopCard> findBySetIdAndRarity(Long cardSetId, CardRarityEnum rarity) {
        return this.list("cardSet.id = ?1 and rarity = ?2", cardSetId, rarity);
    }
}
