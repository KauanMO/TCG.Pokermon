package rest;

import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import models.Deck;
import rest.dtos.deck.CreateDeckDTO;
import rest.dtos.deck.OutDeckDTO;
import services.DeckService;

@Path("decks")
public class DeckResource {
    @Inject
    private DeckService service;

    @POST
    @Path("{userId}")
    public Response createDeck(CreateDeckDTO dto, Long userId) {
        Deck newDeck = service.createDeck(dto, userId);

        return Response
                .ok(new OutDeckDTO(newDeck))
                .build();
    }
}