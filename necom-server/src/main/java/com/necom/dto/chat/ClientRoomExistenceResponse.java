package com.necom.dto.chat;

import lombok.Data;

import java.util.List;

@Data
public class ClientRoomExistenceResponse {
    private boolean roomExistence;
    private RoomResponse roomResponse;
    private List<MessageResponse> roomRecentMessages;
    // Chatbot có đang bật không (có cấu hình API) để client hiển thị đúng lời chào
    private boolean botEnabled;
}
