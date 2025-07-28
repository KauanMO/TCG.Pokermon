package rest;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import models.Deck;
import rest.dtos.deck.CreateDeckDTO;
import rest.dtos.deck.DeckExtraInfoDTO;
import rest.dtos.deck.OutDeckDTO;
import services.DeckService;

import java.util.List;

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

    @GET
    public Response getUserDecks(@QueryParam("userId") Long userId) {
        List<DeckExtraInfoDTO> userDecks = service.findByUserId(userId);

        return Response
                .ok(userDecks)
                .build();
    }
}