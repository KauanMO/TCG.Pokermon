package rest;

import enums.CardTypeEnum;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import models.Card;
import rest.dtos.card.OpenedCardDTO;
import rest.dtos.card.OutCardDTO;
import services.CardService;

import java.util.List;

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
    @Path("open-set/{setId}")
    public Response openSet(Long setId, @QueryParam("amount") Integer amount) {
        List<Card> openedCards = service.openCardSet(setId, amount);

        return Response
                .ok(openedCards.stream().map(OutCardDTO::new))
                .build();
    }

    @GET
    @Path("my-cards")
    public Response getMyCards(@QueryParam("orderBy") String orderBy,
                               @QueryParam("asc") Boolean asc,
                               @QueryParam("cardTypes") List<CardTypeEnum> cardTypes,
                               @QueryParam("page") Integer page,
                               @QueryParam("pageSize") Integer pageSize) {
        var cards = service.findMyCards(orderBy, asc, cardTypes, page, pageSize);

        return Response.
                ok(cards.stream()
                        .map(OutCardDTO::new)
                        .toList())
                .build();
    }
}