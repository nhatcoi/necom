package com.necom.dto.chat;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Gói tin gửi qua STOMP tới /chat/receive/{roomId} (khách) và /chat/receive/admin (nhân viên).
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChatEvent {
    private Kind kind;
    private Long roomId;
    private MessageResponse message;
    private RoomResponse room;
    private Typing typing;

    public enum Kind {
        MESSAGE,
        ROOM,
        TYPING
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Typing {
        private String senderType;
        private String name;
        private boolean active;
    }

    public static ChatEvent message(MessageResponse message) {
        return new ChatEvent(Kind.MESSAGE, message.getRoomId(), message, null, null);
    }

    public static ChatEvent room(RoomResponse room) {
        return new ChatEvent(Kind.ROOM, room.getId(), null, room, null);
    }

    public static ChatEvent typing(Long roomId, Typing typing) {
        return new ChatEvent(Kind.TYPING, roomId, null, null, typing);
    }
}
