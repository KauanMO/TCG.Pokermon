package rest;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import services.CardSetService;

@Path("sets")
public class CardSetResource {
    @Inject
    private CardSetService service;

    @GET
    public Response getCardSets() {
        var cardSets = service.findCardSets();

        return Response.ok(cardSets).build();
    }
}