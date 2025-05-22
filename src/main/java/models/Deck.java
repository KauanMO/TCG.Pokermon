package models;

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
public class Deck {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    public String name;
    public LocalDate createdDate;
    public Boolean active;

    @ManyToOne
    public User user;

    @OneToMany(mappedBy = "deck")
    public final List<DeckCard> cards = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        setCreatedDate(LocalDate.now());
        setActive(true);
    }
}