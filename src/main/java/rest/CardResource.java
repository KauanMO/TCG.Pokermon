package rest;

import enums.CardTypeEnum;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import models.Card;
import rest.dtos.card.MyCardsDTO;
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
        var cardsFound = service.findMyCards(orderBy == null ? "createdDate" : orderBy,
                asc != null && asc,
                cardTypes,
                page,
                pageSize);

        var cardsDTO = cardsFound.cards().stream()
                .map(OutCardDTO::new)
                .toList();

        return Response.
                ok(new MyCardsDTO(cardsDTO, cardsFound.totalCards()))
                .build();
    }

    @GET
    @Path("cards-obtained-set")
    public Response getCardsObtainedByCardSet(@QueryParam("cardSetId") Long cardSetId) {
        var cardsCount = service.getCardsObtainedByCardSet(cardSetId);

        return Response
                .ok(cardsCount)
                .build();
    }

    @DELETE
    @Path("sell/{id}")
    public Response sellCard(Long id) {
        service.sellCard(id);

        return Response
                .noContent()
                .build();
    }
}