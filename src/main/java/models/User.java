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
@Table(name = "app_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    public String username;
    public String email;
    public String password;
    public Boolean active;
    public LocalDate createdDate;
    public Double balance;

    @OneToMany(mappedBy = "user")
    public List<Card> cards = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    public List<Deck> decks = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        setActive(true);
        setCreatedDate(LocalDate.now());
        setBalance(20.0);
    }
}