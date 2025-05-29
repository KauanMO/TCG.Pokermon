package repositories;

import enums.CardTypeEnum;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import models.Card;
import rest.dtos.card.ShopCardCountDTO;
import services.dtos.MyCardsDTO;

import java.util.List;

@ApplicationScoped
public class CardRepository implements PanacheRepository<Card> {
    public List<Card> findByUserId(Long userId) {
        return this.list("user.id = ?1", userId);
    }

    public MyCardsDTO findByUserIdOrderBy(Long userId, String orderBy, Boolean asc, Integer page, Integer pageSize) {
        String order = (asc == null || asc) ? "asc" : "desc";

        var cards = this.find("user.id = ?1 order by " + orderBy + " " + order, userId);

        cards.page(page, pageSize);

        return new MyCardsDTO(cards.list(), (int) cards.count());
    }

    public MyCardsDTO findByUserIdOrderByCardType(Long userId, String orderBy, Boolean asc, List<CardTypeEnum> cardTypes, Integer page, Integer pageSize) {
        String order = (asc == null || asc) ? "asc" : "desc";

        var cards = this.find("user.id = ?1 and shopCard.types in (?2) order by " + orderBy + " " + order,
                userId,
                cardTypes);

        cards.page(page, pageSize);

        return new MyCardsDTO(cards.list(), (int) cards.count());
    }

    public List<ShopCardCountDTO> countCardsByUserAndCardSet(Long userId, Long cardSetId) {
        return getEntityManager()
                .createQuery("""
                            SELECT new rest.dtos.card.ShopCardCountDTO(sc.id, COUNT(c.id))
                            FROM Card c
                            JOIN c.shopCard sc
                            WHERE c.user.id = :userId AND sc.cardSet.id = :cardSetId
                            GROUP BY sc.id
                        """, ShopCardCountDTO.class)
                .setParameter("userId", userId)
                .setParameter("cardSetId", cardSetId)
                .getResultList();
    }
}