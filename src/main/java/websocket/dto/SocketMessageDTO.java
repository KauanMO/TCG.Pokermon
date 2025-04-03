package websocket.dto;

import websocket.enums.SocketMessageTypeEnum;

import java.util.List;
import java.util.Set;

public record SocketMessageDTO(
        SocketMessageTypeEnum type,
        ManagedUserDTO user,
        String message,
        List<String> players,
        List<OutCardDTO> cards,
        ManagedUserDTO nextPlayer
) {
    public SocketMessageDTO(SocketMessageTypeEnum type, ManagedUserDTO user, Set<ManagedUserDTO> players) {
        this(type, user, null, players.stream().map(ManagedUserDTO::username).toList(), null, null);
    }

    public SocketMessageDTO(SocketMessageTypeEnum type, ManagedUserDTO user, List<OutCardDTO> cards, ManagedUserDTO nextPlayer) {
        this(type, user, null, null, cards, nextPlayer);
    }

    public SocketMessageDTO(SocketMessageTypeEnum type) {
        this(type, null, null, null, null, null);
    }

    public SocketMessageDTO(SocketMessageTypeEnum type, List<OutCardDTO> cards) {
        this(type, null, null, null, cards, null);
    }

    public SocketMessageDTO(SocketMessageTypeEnum type, String message) {
        this(type, null, message, null, null, null);
    }
}