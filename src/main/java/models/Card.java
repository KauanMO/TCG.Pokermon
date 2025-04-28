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

    public Double quality;
    public LocalDate createdDate;
    public Double price;

    @ManyToOne
    public User user;

    @ManyToOne
    public ShopCard shopCard;

    @OneToMany(mappedBy = "card")
    public List<DeckCard> decks = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        setCreatedDate(LocalDate.now());
    }
}