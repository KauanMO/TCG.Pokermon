package repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import models.CardSubtype;

@ApplicationScoped
public class CardSubtypeRepository implements PanacheRepository<CardSubtype> {
}
