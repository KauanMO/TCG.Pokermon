package models;

import enums.CardRarityEnum;
import enums.CardSubtypeEnum;
import enums.CardTypeEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShopCard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    public String name;
    public String externalCode;
    public CardRarityEnum rarity;
    public String smallImage;
    public String largeImage;
    public String descripton;
    public String evolvesFrom;

    public Double averagePrice;

    public final List<CardTypeEnum> types = new ArrayList<>();

    public final List<CardSubtypeEnum> subtypes = new ArrayList<>();

    @OneToMany(mappedBy = "shopCard")
    public List<Card> acquiredCards;

    @ManyToOne
    public CardSet cardSet;
}