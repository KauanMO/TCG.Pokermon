package websocket;

import io.quarkus.websockets.next.OpenConnections;
import io.quarkus.websockets.next.WebSocketConnection;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import models.Deck;
import services.DeckService;
import websocket.dto.ManagedUserDTO;
import websocket.dto.OutCardDTO;
import websocket.dto.SocketMessageDTO;
import websocket.enums.SocketMessageTypeEnum;

import java.util.*;

@ApplicationScoped
public class RoundManager {
    Map<ManagedUserDTO, Stack<OutCardDTO>> decksByUsers = new HashMap<>();

    @Inject
    OpenConnections openConnections;

    @Inject
    private DeckService deckService;

    @Inject
    private WebSocketConnection connection;

    Map<ManagedUserDTO, Boolean> readyPlayers = new HashMap<>();

    public void startRound(Map<ManagedUserDTO, Boolean> readyPlayers) {
        this.readyPlayers = readyPlayers;

        buyFirstCards();
        requestFirstBet();
    }

    private void buyFirstCards() {
        for (ManagedUserDTO userDTO : readyPlayers.keySet()) {
            decksByUsers.put(userDTO, getDeck(userDTO.id()));

            List<OutCardDTO> cards = List.of(
                    decksByUsers.get(userDTO).pop(),
                    decksByUsers.get(userDTO).pop(),
                    decksByUsers.get(userDTO).pop()
            );

            openConnections.findByConnectionId(userDTO.connectionId())
                    .ifPresentOrElse(c -> c.sendTextAndAwait(cards),
                            () -> {
                            });
        }
    }

    private Stack<OutCardDTO> getDeck(Long userId) {
        Deck deckFound = deckService.findActiveUserDeck(1L);

        Stack<OutCardDTO> playingDeck = new Stack<>();

        playingDeck.addAll(deckFound
                .getCards()
                .stream()
                .map(dc -> new OutCardDTO(dc.getCard()))
                .toList());

        Collections.shuffle(playingDeck);

        return playingDeck;
    }

    private void requestFirstBet() {
        Random r = new Random();

        Set<ManagedUserDTO> usersKeys = new HashSet<>(readyPlayers.keySet());
        ManagedUserDTO randomUser = readyPlayers.keySet().stream().toList().get(r.nextInt(usersKeys.size()));

        connection.broadcast()
                .sendTextAndAwait(new SocketMessageDTO(
                        SocketMessageTypeEnum.REQUEST_BET,
                        randomUser,
                        usersKeys
                ));
    }
}