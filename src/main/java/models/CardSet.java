package models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardSet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    public String externalId;
    public String name;
    public String series;
    public String symbol;
    public String logo;

    public Double price;

    public String firstCardImage;
    public String secondCardImage;
    public String thirdCardImage;

    @OneToMany
    public List<ShopCard> shopCards;
}