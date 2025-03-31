package repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import models.CardSet;

import java.util.Optional;

@ApplicationScoped
public class CardSetRepository implements PanacheRepository<CardSet> {
    public Optional<CardSet> findByExternalId(String externalId) {
        return this
                .find("externalid = ?1", externalId)
                .stream()
                .findAny();
    }
}