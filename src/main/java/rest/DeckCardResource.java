package rest;

import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import rest.dtos.deckcard.CreateDeckCardDTO;
import services.DeckCardService;

@Path("deck-cards")
public class DeckCardResource {
    @Inject
    private DeckCardService service;

    @POST
    @Path("{userId}")
    public Response createDeckCards(CreateDeckCardDTO dto) {
        var validatedCards = service.createDeckCards(dto);

        return Response
                .ok(validatedCards)
                .build();
    }
}
