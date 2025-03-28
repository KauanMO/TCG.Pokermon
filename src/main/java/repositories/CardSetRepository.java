package repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import models.CardSet;

@ApplicationScoped
public class CardSetRepository implements PanacheRepository<CardSet> {
}