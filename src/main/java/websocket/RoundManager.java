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
import websocket.exceptions.MoreThanOneCardChangeException;
import websocket.exceptions.WrongUserTurnException;

import java.util.*;

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
    private final Map<ManagedUserDTO, List<OutCardDTO>> handsByUsers = new HashMap<>();
    private Map<ManagedUserDTO, Boolean> readyPlayers = new HashMap<>();
    private final Queue<ManagedUserDTO> playersQueue = new LinkedList<>();
    private Map<ManagedUserDTO, List<OutCardDTO>> cardBank;
    private Map<CurrentQueueSpin, Integer> spinCount = Map.of(
            CurrentQueueSpin.CHANGING, 0
    );

    public enum CurrentQueueSpin {
        CHANGING
    }

    public void startRound(Map<ManagedUserDTO, Boolean> readyPlayers) {
        this.readyPlayers = readyPlayers;
        this.cardBank = new HashMap<>();

        resetPlayersQueue();
        buyFirstCards();
        sendRequest(SocketMessageTypeEnum.REQUEST_CARD_BET);
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

        popPlayersQueue(SocketMessageTypeEnum.REQUEST_CARD_TRADE);

        return new BettedCardsDTO(cards, playersQueue.peek());
    }

    public ManagedUserDTO tradeCard(List<Long> cardsIds, User user) {
        if (cardsIds.size() > 1) throw new MoreThanOneCardChangeException();

        ManagedUserDTO currentUser = playersQueue.peek();

        if(!user.getId().equals(currentUser.id())) throw new WrongUserTurnException();

        OutCardDTO card = handsByUsers.get(currentUser)
                .stream().filter(c -> c
                        .id()
                        .equals(cardsIds.getFirst()))
                .findFirst()
                .orElseThrow();

        handsByUsers.get(currentUser).remove(card);

        buyCards(1, currentUser);

        broadcastSingleConnection(currentUser, new SocketMessageDTO(
                SocketMessageTypeEnum.VIEW_HAND,
                handsByUsers.get(currentUser)
        ));

        popPlayersQueue(SocketMessageTypeEnum.REQUEST_CARD_BET);

        return playersQueue.peek();
    }

    public void viewHand(User user) {
        ManagedUserDTO currentUser = new ManagedUserDTO(user, connection.id());

        List<OutCardDTO> cards = handsByUsers.get(currentUser);

        broadcastSingleConnection(currentUser, new SocketMessageDTO(
                SocketMessageTypeEnum.VIEW_HAND,
                cards
        ));
    }

    private void popPlayersQueue(SocketMessageTypeEnum nextSpinRequest) {
        playersQueue.poll();

        if (playersQueue.peek() == null) {
            resetPlayersQueue();

            sendRequest(nextSpinRequest);
        }
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
            handsByUsers.put(userDTO, new ArrayList<>());

            buyCards(3, userDTO);

            broadcastSingleConnection(userDTO, new SocketMessageDTO(
                    SocketMessageTypeEnum.VIEW_HAND,
                    handsByUsers.get(userDTO)
            ));
        }
    }

    private void broadcastSingleConnection(ManagedUserDTO user, SocketMessageDTO message) {
        openConnections.findByConnectionId(user.connectionId())
                .ifPresentOrElse(c -> c.sendTextAndAwait(message), () -> {
                });
    }

    private void buyCards(Integer amount, ManagedUserDTO user) {
        for (int i = 0; i < amount; i++) handsByUsers.get(user).add(decksByUsers.get(user).pop());
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

    private void sendRequest(SocketMessageTypeEnum requestType) {
        connection.broadcast()
                .sendTextAndAwait(new SocketMessageDTO(
                        requestType,
                        playersQueue.peek(),
                        new HashSet<>(playersQueue)
                ));
    }
}