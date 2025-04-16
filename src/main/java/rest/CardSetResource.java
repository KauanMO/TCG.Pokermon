package rest;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import rest.dtos.cardSet.CreateCardSetDTO;
import services.CardSetService;

@Path("cardsets")
public class CardSetResource {
    @Inject
    private CardSetService service;

    @POST
    @RolesAllowed("ADMIN")
    public Response createCardSet(CreateCardSetDTO dto) {
        var cardSet = service.createCardSet(dto);

        return Response
                .ok(cardSet)
                .build();
    }

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