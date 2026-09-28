package com.necom.controller.client;

import com.necom.constant.AppConstants;
import com.necom.dto.chat.ClientRoomExistenceResponse;
import com.necom.dto.chat.MessageResponse;
import com.necom.dto.chat.RoomResponse;
import com.necom.service.chat.ChatService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/client-api/chat")
@AllArgsConstructor
@CrossOrigin(AppConstants.FRONTEND_HOST)
public class ClientChatController {

    private ChatService chatService;

    @GetMapping("/get-room")
    public ResponseEntity<ClientRoomExistenceResponse> getRoom(Authentication authentication) {
        RoomResponse roomResponse = chatService.findCustomerRoom(authentication.getName());

        var clientRoomExistenceResponse = new ClientRoomExistenceResponse();
        clientRoomExistenceResponse.setRoomExistence(roomResponse != null);
        clientRoomExistenceResponse.setRoomResponse(roomResponse);
        clientRoomExistenceResponse.setRoomRecentMessages(roomResponse != null
                ? chatService.listMessages(authentication, roomResponse.getId(), null, null, 30)
                : Collections.emptyList());
        clientRoomExistenceResponse.setBotEnabled(chatService.isBotActive());

        return ResponseEntity.status(HttpStatus.OK).body(clientRoomExistenceResponse);
    }

    @PostMapping("/create-room")
    public ResponseEntity<RoomResponse> createRoom(Authentication authentication) {
        return ResponseEntity.status(HttpStatus.OK).body(chatService.getOrCreateRoom(authentication.getName()));
    }

    // before: cuộn xem tin cũ; after: lấy tin bị lỡ khi kết nối lại
    @GetMapping("/messages")
    public ResponseEntity<List<MessageResponse>> getMessages(Authentication authentication,
                                                             @RequestParam Long roomId,
                                                             @RequestParam(required = false) @Nullable Long before,
                                                             @RequestParam(required = false) @Nullable Long after,
                                                             @RequestParam(defaultValue = "30") int size) {
        return ResponseEntity.ok(chatService.listMessages(authentication, roomId, before, after, size));
    }

    @PostMapping("/request-agent")
    public ResponseEntity<RoomResponse> requestAgent(Authentication authentication) {
        return ResponseEntity.ok(chatService.requestAgent(authentication));
    }

    @PostMapping("/read")
    public ResponseEntity<Map<String, Object>> markRead(Authentication authentication, @RequestParam Long roomId) {
        chatService.markRead(authentication, roomId);
        // Trả JSON rỗng thay vì 204 để client dùng chung postWithToken (luôn parse JSON)
        return ResponseEntity.ok(Collections.emptyMap());
    }

    @PostMapping("/resolve")
    public ResponseEntity<RoomResponse> resolve(Authentication authentication, @RequestParam Long roomId) {
        return ResponseEntity.ok(chatService.resolve(authentication, roomId));
    }

}
