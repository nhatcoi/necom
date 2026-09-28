package com.necom.controller.chat;

import com.necom.constant.AppConstants;
import com.necom.constant.FieldName;
import com.necom.constant.ResourceName;
import com.necom.dto.chat.ChatCustomerProfileResponse;
import com.necom.dto.chat.MessageResponse;
import com.necom.dto.chat.RoomResponse;
import com.necom.entity.authentication.User;
import com.necom.entity.chat.Room;
import com.necom.entity.order.Order;
import com.necom.exception.ResourceNotFoundException;
import com.necom.repository.chat.RoomRepository;
import com.necom.repository.order.OrderRepository;
import com.necom.repository.reward.RewardLogRepository;
import com.necom.service.chat.ChatService;
import com.necom.service.chat.ChatbotService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * API inbox CSKH cho nhân viên. Đường dẫn /api/chat/** yêu cầu ADMIN hoặc EMPLOYEE (SecurityConstants).
 */
@RestController
@RequestMapping("/api/chat")
@AllArgsConstructor
@CrossOrigin(AppConstants.FRONTEND_HOST)
public class AdminChatController {

    private ChatService chatService;
    private ChatbotService chatbotService;
    private RoomRepository roomRepository;
    private OrderRepository orderRepository;
    private RewardLogRepository rewardLogRepository;

    @GetMapping("/rooms")
    public ResponseEntity<List<RoomResponse>> getRooms() {
        return ResponseEntity.ok(chatService.listInboxRooms());
    }

    @GetMapping("/rooms/{roomId}/messages")
    public ResponseEntity<List<MessageResponse>> getMessages(Authentication authentication,
                                                             @PathVariable Long roomId,
                                                             @RequestParam(required = false) @Nullable Long before,
                                                             @RequestParam(required = false) @Nullable Long after,
                                                             @RequestParam(defaultValue = "50") int size) {
        return ResponseEntity.ok(chatService.listMessages(authentication, roomId, before, after, size));
    }

    @PostMapping("/rooms/{roomId}/claim")
    public ResponseEntity<RoomResponse> claim(Authentication authentication, @PathVariable Long roomId) {
        return ResponseEntity.ok(chatService.claim(authentication, roomId));
    }

    @PostMapping("/rooms/{roomId}/release")
    public ResponseEntity<RoomResponse> releaseToBot(@PathVariable Long roomId) {
        return ResponseEntity.ok(chatService.releaseToBot(roomId));
    }

    @PostMapping("/rooms/{roomId}/resolve")
    public ResponseEntity<RoomResponse> resolve(Authentication authentication, @PathVariable Long roomId) {
        return ResponseEntity.ok(chatService.resolve(authentication, roomId));
    }

    @PostMapping("/rooms/{roomId}/read")
    public ResponseEntity<Map<String, Object>> markRead(Authentication authentication, @PathVariable Long roomId) {
        chatService.markRead(authentication, roomId);
        // Trả JSON rỗng thay vì 204 để client dùng chung postWithToken (luôn parse JSON)
        return ResponseEntity.ok(Collections.emptyMap());
    }

    @PostMapping("/rooms/{roomId}/notes")
    public ResponseEntity<MessageResponse> addNote(Authentication authentication, @PathVariable Long roomId,
                                                   @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(chatService.addInternalNote(authentication, roomId, body.get("content")));
    }

    @PostMapping("/rooms/{roomId}/order-card")
    public ResponseEntity<MessageResponse> sendOrderCard(Authentication authentication, @PathVariable Long roomId,
                                                         @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(chatService.sendOrderCard(authentication, roomId, body.get("code")));
    }

    @GetMapping("/rooms/{roomId}/suggest")
    public ResponseEntity<Map<String, String>> suggest(@PathVariable Long roomId) {
        return ResponseEntity.ok(Collections.singletonMap("content", chatbotService.suggestReply(roomId)));
    }

    @GetMapping("/rooms/{roomId}/customer")
    @Transactional(readOnly = true)
    public ResponseEntity<ChatCustomerProfileResponse> getCustomer(@PathVariable Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceName.ROOM, FieldName.ID, roomId));
        User user = room.getUser();

        Page<Order> orders = orderRepository.findAllByUsername(user.getUsername(), "id,desc", null, PageRequest.of(0, 5));
        List<ChatCustomerProfileResponse.OrderSummary> recentOrders = orders.getContent().stream()
                .map(order -> new ChatCustomerProfileResponse.OrderSummary(
                        order.getId(),
                        order.getCode(),
                        order.getStatus(),
                        ChatService.orderStatusLabel(order.getStatus()),
                        order.getTotalPay(),
                        order.getCreatedAt()))
                .collect(Collectors.toList());

        int rewardScore = rewardLogRepository.sumScoreByUsername(user.getUsername());

        return ResponseEntity.ok(new ChatCustomerProfileResponse(
                user.getId(),
                user.getUsername(),
                user.getFullname(),
                user.getEmail(),
                user.getPhone(),
                user.getCreatedAt(),
                rewardScore,
                orders.getTotalElements(),
                recentOrders));
    }

}
