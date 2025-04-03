package websocket;

import io.quarkus.websockets.next.OpenConnections;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import models.Deck;
import services.DeckService;
import websocket.dto.ManagedUserDTO;
import websocket.dto.OutCardDTO;

import java.util.*;

@ApplicationScoped
public class RoundManager {
    Map<ManagedUserDTO, Stack<OutCardDTO>> decksByUsers = new HashMap<>();

    @Inject
    OpenConnections openConnections;

    @Inject
    private DeckService deckService;

    public void startRound(Map<ManagedUserDTO, Boolean> readyPlayers) {
        buyFirstCards(readyPlayers);
    }

    private void buyFirstCards(Map<ManagedUserDTO, Boolean> readyPlayers) {
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
}
