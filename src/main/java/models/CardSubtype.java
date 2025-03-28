package models;

import enums.CardSubtypeEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardSubtype {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    public CardSubtypeEnum subtype;

    @ManyToOne
    public Card card;
}