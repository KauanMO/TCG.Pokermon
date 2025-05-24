package repositories;

import enums.CardTypeEnum;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import models.Card;

import java.util.List;
import java.util.Map;

@ApplicationScoped
public class CardRepository implements PanacheRepository<Card> {
    public List<Card> findByUserId(Long userId) {
        return this.list("user.id = ?1", userId);
    }

    public List<Card> findByUserIdOrderBy(Long userId, String orderBy, Boolean asc, Integer page, Integer pageSize) {
        String order = (asc == null || asc) ? "asc" : "desc";

        var cards = this.find("user.id = ?1 order by " + orderBy + " " + order, userId);

        cards.page(page, pageSize);

        return cards.list();
    }

    public List<Card> findByUserIdOrderByCardType(Long userId, String orderBy, Boolean asc, List<CardTypeEnum> cardTypes, Integer page, Integer pageSize) {
        String order = (asc == null || asc) ? "asc" : "desc";

        var cards = this.find("user.id = ?1 and shopCard.types in (?2) order by " + orderBy + " " + order,
                userId,
                cardTypes);

        cards.page(page, pageSize);

        return cards.list();
    }
}