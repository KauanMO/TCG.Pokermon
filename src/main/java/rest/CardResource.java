package rest;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import rest.dtos.card.OpenedCardDTO;
import rest.dtos.card.OutCardDTO;
import services.CardService;

@Path("cards")
public class CardResource {
    @Inject
    private CardService service;

    @GET
    public Response getByName(@QueryParam("name") String name) {
        var cards = service.getCardsByName(name);

        return Response.ok(cards).build();
    }

    @GET
    @Path("random")
    public Response getRandomCard(@QueryParam("rarity") String rarity) {
        var card = service.getRandomCard(rarity);

        return Response.ok(card).build();
    }

    @POST
    @Path("open-set/{setExternalId}")
    public Response openSet(String setExternalId) {
        var openedCard = service.openCardSet(setExternalId);

        return Response
                .ok(new OpenedCardDTO(openedCard))
                .build();
    }

    @GET
    @Path("my-cards")
    public Response getMyCards() {
        var cards = service.findMyCards();

        return Response.
                ok(cards.stream()
                        .map(OutCardDTO::new)
                        .toList())
                .build();
    }
}