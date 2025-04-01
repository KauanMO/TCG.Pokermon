package models;

import enums.CardRarityEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Card {
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

    public String setName;
    public String setId;

    public Double price;
    public Double quality;
    public LocalDate createdDate;

    @ManyToOne
    public User user;

    @OneToMany(mappedBy = "card")
    public List<CardType> types = new ArrayList<>();

    @OneToMany(mappedBy = "card")
    public List<CardSubtype> subtypes = new ArrayList<>();

    @OneToMany(mappedBy = "card")
    public List<DeckCard> decks = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        setPrice();
        setCreatedDate(LocalDate.now());
    }
}