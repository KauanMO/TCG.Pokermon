package websocket.dto;

import websocket.enums.SocketMessageTypeEnum;

public record SocketMessageDTO(
        SocketMessageTypeEnum type,
        String username,
        String message,
        OutCardDTO card
) {
    public SocketMessageDTO(SocketMessageTypeEnum type, String username) {
        this(type, username, null, null);
    }

    public SocketMessageDTO(SocketMessageTypeEnum type, String username, OutCardDTO card) {
        this(type, username, null, card);
    }
}