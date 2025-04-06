package rest;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import services.CardSetService;

@Path("cardsets")
public class CardSetResource {
    @Inject
    private CardSetService service;

    @GET
    public Response getCardSets() {
        var cardSets = service.findCardSets();

        return Response.ok(cardSets).build();
    }

    @GET
    @Path("{id}")
    public Response getById(Long id, @QueryParam("page") Integer page, @QueryParam("pageSize") Integer pageSize) {
        return Response
                .ok(service.findByIdWithCards(id, page, pageSize))
                .build();
    }
}