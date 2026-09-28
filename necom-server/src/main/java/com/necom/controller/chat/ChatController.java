package com.necom.controller.chat;

import com.necom.dto.chat.ChatSendRequest;
import com.necom.entity.chat.SenderType;
import com.necom.service.chat.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final ChatService chatService;

    // Người gửi lấy từ Principal (JWT ở frame CONNECT), phòng lấy từ destination — không tin payload
    @MessageMapping("/{roomId}")
    public void sendMessage(@DestinationVariable Long roomId, @Payload ChatSendRequest request, Principal principal) {
        chatService.send((Authentication) principal, roomId, request);
    }

    @MessageMapping("/{roomId}/typing")
    public void typing(@DestinationVariable Long roomId, @Payload Map<String, Object> body, Principal principal) {
        Authentication authentication = (Authentication) principal;
        if (!chatService.canAccessRoom(authentication, roomId)) {
            return;
        }
        boolean active = Boolean.TRUE.equals(body.get("active"));
        boolean staff = ChatService.isStaff(authentication);
        // Khách gõ: chỉ báo cho nhân viên; nhân viên gõ: báo cho khách
        chatService.publishTyping(roomId, staff ? SenderType.AGENT : SenderType.CUSTOMER,
                authentication.getName(), active, staff);
    }

    @MessageExceptionHandler
    public void handleException(Exception e) {
        log.warn("Chat STOMP error: {}", e.getMessage());
    }

}
