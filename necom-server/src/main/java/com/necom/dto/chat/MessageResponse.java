package com.necom.dto.chat;

import com.fasterxml.jackson.databind.JsonNode;
import com.necom.entity.chat.MessageType;
import com.necom.entity.chat.SenderType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageResponse {
    private Long id;
    private Instant createdAt;
    private Instant updatedAt;
    private String content;
    private Integer status;
    // Null với tin của bot và tin hệ thống
    private UserResponse user;
    private Long roomId;
    private MessageType type;
    private SenderType senderType;
    private JsonNode payload;
    private String clientMsgId;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserResponse {
        private Long id;
        private String username;
        private String fullname;
        private String email;
    }
}
