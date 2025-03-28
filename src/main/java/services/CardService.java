package services;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import rest.clients.CardsRestClient;
import rest.dtos.card.ExternalCardDTO;
import rest.dtos.external.ExternalCardResponseDTO;

import java.util.Set;

@ApplicationScoped
public class CardService {
    @RestClient
    CardsRestClient cardsRestClient;

    public Set<ExternalCardDTO> getCardsByName(String name) {
        ExternalCardResponseDTO externalResponse = cardsRestClient.getByName("name:camerupt", "id,name,images,rarity,set,prices");

        return externalResponse.data();
    }
}