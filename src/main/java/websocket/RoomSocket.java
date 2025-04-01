package websocket;

import io.quarkus.websockets.next.OnOpen;
import io.quarkus.websockets.next.OnTextMessage;
import io.quarkus.websockets.next.WebSocket;
import io.quarkus.websockets.next.WebSocketConnection;
import io.vertx.core.json.Json;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import models.Card;
import models.User;
import services.CardService;
import services.UserService;
import services.exceptions.UserNotFoundException;
import websocket.dto.OutCardDTO;
import websocket.dto.SocketMessageDTO;
import websocket.enums.SocketMessageTypeEnum;

@WebSocket(path = "/room/{userId}")
public class RoomSocket {
    @Inject
    WebSocketConnection connection;

    @Inject
    private UserService userService;

    @Inject
    private CardService cardService;

    @Transactional
    @OnOpen(broadcast = true)
    public SocketMessageDTO onOpen() {
        User userFound = userService
                .findUserById(Long.valueOf(connection.pathParam("userId")))
                .orElseThrow(UserNotFoundException::new);

        return new SocketMessageDTO(SocketMessageTypeEnum.USER_JOINED, userFound.getUsername());
    }

    @Transactional
    @OnTextMessage(broadcast = true)
    public SocketMessageDTO onMessage(String stringMessage) {
        User userFound = userService
                .findUserById(Long.valueOf(connection.pathParam("userId")))
                .orElseThrow(UserNotFoundException::new);

        SocketMessageDTO message = Json.decodeValue(stringMessage, SocketMessageDTO.class);

        if (message.type().equals(SocketMessageTypeEnum.BET_CARD)) {
            Card bettedCard = cardService.findCardById(message.card().id());

            return new SocketMessageDTO(SocketMessageTypeEnum.BET_CARD,
                    userFound.getUsername(),
                    new OutCardDTO(bettedCard));
        }

        return null;
    }
}