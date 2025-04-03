package websocket;

import io.quarkus.websockets.next.OpenConnections;
import io.quarkus.websockets.next.WebSocketConnection;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import models.Card;
import models.Deck;
import models.User;
import services.CardService;
import services.DeckService;
import websocket.dto.BettedCardsDTO;
import websocket.dto.ManagedUserDTO;
import websocket.dto.OutCardDTO;
import websocket.dto.SocketMessageDTO;
import websocket.enums.SocketMessageTypeEnum;
import websocket.exceptions.BettedCardsValueNotEnoughException;
import websocket.exceptions.WrongUserTurnException;

import java.util.*;
import java.util.stream.DoubleStream;

@ApplicationScoped
public class RoundManager {
    @Inject
    private OpenConnections openConnections;
    @Inject
    private DeckService deckService;
    @Inject
    private CardService cardService;
    @Inject
    private WebSocketConnection connection;

    private final Map<ManagedUserDTO, Stack<OutCardDTO>> decksByUsers = new HashMap<>();
    private Map<ManagedUserDTO, Boolean> readyPlayers = new HashMap<>();
    private final Queue<ManagedUserDTO> playersQueue = new LinkedList<>();
    private Map<ManagedUserDTO, List<OutCardDTO>> cardBank;

    public void startRound(Map<ManagedUserDTO, Boolean> readyPlayers) {
        this.readyPlayers = readyPlayers;
        this.cardBank = new HashMap<>();

        resetPlayersQueue();
        buyFirstCards();
        requestFirstBet();
    }

    public BettedCardsDTO betCard(List<Long> cardsIds, User user) {
        ManagedUserDTO nextPlayer = playersQueue.peek();

        if (nextPlayer == null || !nextPlayer
                .id()
                .equals(user.getId()))
            throw new WrongUserTurnException();

        List<Card> cards = cardService.findCardsByIds(cardsIds);

        double cardBankValue = calculateCardBankValue();
        double bettedCardsValue = cards.stream().mapToDouble(Card::getPrice).sum();

        if (bettedCardsValue < cardBankValue * 0.9)
            throw new BettedCardsValueNotEnoughException(bettedCardsValue, cardBankValue * 0.9);

        if (bettedCardsValue > cardBankValue * 1.5) resetPlayersQueue();

        if (!cardBank.containsKey(nextPlayer)) cardBank.put(nextPlayer, new ArrayList<>());

        cardBank.get(nextPlayer).addAll(cards.stream()
                .map(OutCardDTO::new)
                .toList());

        playersQueue.poll();

        return new BettedCardsDTO(cards, playersQueue.peek());
    }

    private Double calculateCardBankValue() {
        if (cardBank.isEmpty()) return 0.0;

        return cardBank.values()
                .stream()
                .flatMap(List::stream)
                .mapToDouble(OutCardDTO::price)
                .sum();
    }

    private void resetPlayersQueue() {
        List<ManagedUserDTO> players = new ArrayList<>(readyPlayers
                .keySet().stream()
                .toList());

        for (ManagedUserDTO player : players) {
            if (!playersQueue.contains(player)) playersQueue.add(player);
        }
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
        // TODO Change the findActiveUserDeck param to dynamic userId
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
        connection.broadcast()
                .sendTextAndAwait(new SocketMessageDTO(
                        SocketMessageTypeEnum.REQUEST_BET,
                        playersQueue.peek(),
                        new HashSet<>(playersQueue)
                ));
    }
}