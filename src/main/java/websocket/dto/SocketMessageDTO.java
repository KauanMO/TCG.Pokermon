package websocket.dto;

import websocket.enums.SocketMessageTypeEnum;

import java.util.List;
import java.util.Set;

public record SocketMessageDTO(
        SocketMessageTypeEnum type,
        ManagedUserDTO user,
        String message,
        OutCardDTO card,
        Long nextPlayerId,
        List<String> players,
        List<OutCardDTO> cards
) {
    public SocketMessageDTO(SocketMessageTypeEnum type, ManagedUserDTO user, Set<ManagedUserDTO> players) {
        this(type, user, null, null, null, players.stream().map(ManagedUserDTO::username).toList(), null);
    }

    public SocketMessageDTO(SocketMessageTypeEnum type, ManagedUserDTO user, OutCardDTO card) {
        this(type, user, null, card, null, null, null);
    }

    public SocketMessageDTO(SocketMessageTypeEnum type) {
        this(type, null, null, null, null, null, null);
    }

    public SocketMessageDTO(SocketMessageTypeEnum type, List<OutCardDTO> cards) {
        this(type, null, null, null, null, null, cards);
    }
}