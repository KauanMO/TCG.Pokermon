package websocket;

import io.quarkus.websockets.next.*;
import io.vertx.core.json.Json;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import models.User;
import services.UserService;
import services.exceptions.UserNotFoundException;
import websocket.dto.BettedCardsDTO;
import websocket.dto.ManagedUserDTO;
import websocket.dto.OutCardDTO;
import websocket.dto.SocketMessageDTO;
import websocket.enums.SocketMessageTypeEnum;
import websocket.exceptions.BettedCardsValueNotEnoughException;
import websocket.exceptions.CardNotInDeckException;
import websocket.exceptions.WrongUserTurnException;

import java.util.*;

@WebSocket(path = "/room/{userId}")
public class RoomSocket {
    @Inject
    private WebSocketConnection connection;

    @Inject
    private UserService userService;

    @Inject
    private RoundManager roundManager;

    @Inject
    private OpenConnections openConnections;

    private final Map<ManagedUserDTO, Boolean> readyPlayers = new HashMap<>();

    @Transactional
    @OnOpen(broadcast = true)
    public SocketMessageDTO onOpen() {
        User userFound = userService
                .findUserById(Long.valueOf(connection.pathParam("userId")))
                .orElseThrow(UserNotFoundException::new);

        readyPlayers.put(new ManagedUserDTO(userFound, connection.id()), false);

        return new SocketMessageDTO(SocketMessageTypeEnum.USER_JOINED, new ManagedUserDTO(userFound, connection.id()), readyPlayers.keySet());
    }

    @Transactional
    @OnClose
    public void onClose() {
        User userFound = userService
                .findUserById(Long.valueOf(connection.pathParam("userId")))
                .orElseThrow(UserNotFoundException::new);

        connection.broadcast().sendTextAndAwait(new SocketMessageDTO(SocketMessageTypeEnum.USER_LEFT,
                new ManagedUserDTO(userFound, connection.id()),
                readyPlayers.keySet()));
    }

    @Transactional
    @OnTextMessage(broadcast = true)
    public SocketMessageDTO onMessage(String stringMessage) {
        User userFound = userService
                .findUserById(Long.valueOf(connection.pathParam("userId")))
                .orElseThrow(UserNotFoundException::new);

        SocketMessageDTO message = Json.decodeValue(stringMessage, SocketMessageDTO.class);

        switch (message.type()) {
            case SocketMessageTypeEnum.READY -> {
                return onConfirmReadyMessage(userFound);
            }

            case SocketMessageTypeEnum.CARD_BET -> {
                return onBetCardMessage(message
                                .cards().stream()
                                .map(OutCardDTO::id)
                                .toList(),
                        userFound);
            }

            case SocketMessageTypeEnum.CARD_TRADE -> {
                return onCardTrade(message.cards().stream().
                        map(OutCardDTO::id)
                        .toList(), userFound);
            }

            case SocketMessageTypeEnum.VIEW_HAND -> {
                onViewHand(userFound);

                return null;
            }

            default -> {
                return new SocketMessageDTO(SocketMessageTypeEnum.ERROR);
            }
        }
    }

    private SocketMessageDTO onConfirmReadyMessage(User userFound) {
        readyPlayers.put(new ManagedUserDTO(userFound, connection.id()), true);

        if (!readyPlayers.containsValue(false) && readyPlayers.size() > 1) roundManager.startRound(readyPlayers);

        return new SocketMessageDTO(SocketMessageTypeEnum.READY,
                new ManagedUserDTO(userFound, connection.id()),
                readyPlayers.keySet());
    }

    private SocketMessageDTO onBetCardMessage(List<Long> cardsIds, User userFound) {
        try {
            BettedCardsDTO bettedCards = roundManager.betCard(cardsIds, userFound);

            return new SocketMessageDTO(SocketMessageTypeEnum.CARD_BET,
                    new ManagedUserDTO(userFound, connection.id()),
                    new ArrayList<>(bettedCards
                            .cards().stream()
                            .map(OutCardDTO::new)
                            .toList()),
                    bettedCards.nextPlayer());
        } catch (WrongUserTurnException | BettedCardsValueNotEnoughException e) {
            return new SocketMessageDTO(SocketMessageTypeEnum.ERROR,
                    e.getMessage());
        }
    }

    private SocketMessageDTO onCardTrade(List<Long> cardsIds, User userFound) {
        try {
            ManagedUserDTO nextPlayer = roundManager.tradeCard(cardsIds, userFound);

            return new SocketMessageDTO(SocketMessageTypeEnum.CARD_TRADE,
                    new ManagedUserDTO(userFound, connection.id()),
                    nextPlayer);
        } catch (CardNotInDeckException e) {
            return new SocketMessageDTO(SocketMessageTypeEnum.ERROR,
                    e.getMessage());
        }
    }

    private void onViewHand(User userFound) {
        roundManager.viewHand(userFound);
    }
}