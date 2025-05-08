package rest;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
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
    public Response createDeck(@Valid CreateDeckDTO dto) {
        Deck newDeck = service.createDeck(dto);

        return Response
                .ok(new OutDeckDTO(newDeck))
                .build();
    }
}