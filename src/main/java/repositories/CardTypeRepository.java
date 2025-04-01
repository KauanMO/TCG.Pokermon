package repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import models.CardType;

@ApplicationScoped
public class CardTypeRepository implements PanacheRepository<CardType> {
}