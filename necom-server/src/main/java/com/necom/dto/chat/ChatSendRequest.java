package com.necom.dto.chat;

import lombok.Data;

@Data
public class ChatSendRequest {
    private String content;
    // Id do client sinh ra (UUID), dùng để chống trùng và khớp tin optimistic
    private String clientMsgId;
}
