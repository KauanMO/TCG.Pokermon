package services;

import enums.CardRarity;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import rest.clients.CardsRestClient;
import rest.dtos.card.ExternalCardDTO;
import rest.dtos.external.ExternalCardResponseDTO;
import utils.CardRarityPicker;

import java.util.Objects;
import java.util.Random;
import java.util.Set;

@ApplicationScoped
public class CardService {
    @RestClient
    private CardsRestClient cardsRestClient;

    private final String defaultSelectFilds = "id,name,images,rarity,set,prices";

    public Set<ExternalCardDTO> getCardsByName(String name) {
        ExternalCardResponseDTO externalResponse = cardsRestClient.get("name:" + name, defaultSelectFilds);

        return externalResponse.data();
    }

    public ExternalCardDTO getRandomCard(String rarity) {
        Random random = new Random();

        String queryRarity = "\"" + Objects.requireNonNullElseGet(rarity,
                () -> CardRarityPicker.pickRarity().name()).replace("_", " ") + "\"";

        Integer externalResponseTotal = cardsRestClient
                .get("rarity:" + queryRarity, "id", 1)
                .totalCount();

        Integer randomCardPosition = random.nextInt(externalResponseTotal + 1);

        ExternalCardResponseDTO externalResponse = cardsRestClient.get("rarity:" + queryRarity + " supertype:pokemon",
                defaultSelectFilds,
                1,
                randomCardPosition);

        return externalResponse
                .data()
                .iterator()
                .next();
    }
}