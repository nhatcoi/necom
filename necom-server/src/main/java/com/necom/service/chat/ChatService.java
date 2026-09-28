package com.necom.service.chat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.necom.config.chatbot.ChatbotProperties;
import com.necom.constant.FieldName;
import com.necom.constant.ResourceName;
import com.necom.constant.SecurityConstants;
import com.necom.dto.chat.ChatEvent;
import com.necom.dto.chat.ChatSendRequest;
import com.necom.dto.chat.MessageResponse;
import com.necom.dto.chat.RoomResponse;
import com.necom.dto.general.NotificationResponse;
import com.necom.entity.authentication.User;
import com.necom.entity.chat.Message;
import com.necom.entity.chat.MessageType;
import com.necom.entity.chat.Room;
import com.necom.entity.chat.RoomStatus;
import com.necom.entity.chat.SenderType;
import com.necom.entity.general.Notification;
import com.necom.entity.general.NotificationType;
import com.necom.entity.order.Order;
import com.necom.exception.ResourceNotFoundException;
import com.necom.mapper.chat.MessageMapper;
import com.necom.mapper.chat.RoomMapper;
import com.necom.mapper.general.NotificationMapper;
import com.necom.repository.authentication.UserRepository;
import com.necom.repository.chat.MessageRepository;
import com.necom.repository.chat.RoomRepository;
import com.necom.repository.general.NotificationRepository;
import com.necom.repository.order.OrderRepository;
import com.necom.service.general.NotificationService;
import com.necom.utils.VietnameseTextUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Nghiệp vụ chat CSKH: phân quyền phòng, máy trạng thái BOT → WAITING_AGENT → AGENT → RESOLVED,
 * lưu tin (chống trùng theo clientMsgId) rồi mới broadcast qua STOMP sau khi commit.
 * <p>
 * Kênh STOMP:
 * - /chat/receive/{roomId}: khách (không bao giờ nhận INTERNAL_NOTE)
 * - /chat/receive/admin: toàn bộ sự kiện cho nhân viên
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    public static final String ROOM_TOPIC_PREFIX = "/chat/receive/";
    public static final String ADMIN_TOPIC = "/chat/receive/admin";
    public static final String BOT_NAME = "Trợ lý Necom";

    private static final int MAX_CONTENT_LENGTH = 4000;
    private static final int MAX_PAGE_SIZE = 100;

    private static final Set<SenderType> CUSTOMER_UNREAD_SENDERS = EnumSet.of(SenderType.AGENT, SenderType.BOT);

    // Cụm từ (đã bỏ dấu) cho thấy khách muốn gặp người thật
    private static final List<String> HANDOFF_PHRASES = List.of(
            "tu van vien", "gap nhan vien", "nhan vien tu van", "nhan vien cskh", "cham soc khach hang",
            "nguoi that", "gap nguoi", "noi chuyen voi nguoi", "cskh", "khieu nai", "gap admin", "gap quan ly"
    );

    private final RoomRepository roomRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final NotificationRepository notificationRepository;
    private final MessageMapper messageMapper;
    private final RoomMapper roomMapper;
    private final NotificationMapper notificationMapper;
    private final NotificationService notificationService;
    private final SimpMessagingTemplate messagingTemplate;
    private final ApplicationEventPublisher eventPublisher;
    private final ChatbotProperties chatbotProperties;
    private final ObjectMapper objectMapper;

    // ================= Truy vấn =================

    public static boolean isStaff(Authentication authentication) {
        if (authentication == null) {
            return false;
        }
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            String role = authority.getAuthority();
            if (SecurityConstants.Role.ADMIN.equals(role) || SecurityConstants.Role.EMPLOYEE.equals(role)) {
                return true;
            }
        }
        return false;
    }

    public boolean isBotActive() {
        return chatbotProperties.isActive();
    }

    @Transactional(readOnly = true)
    public boolean canAccessRoom(Authentication authentication, Long roomId) {
        return isStaff(authentication) || roomRepository.existsByIdAndUserUsername(roomId, authentication.getName());
    }

    @Transactional(readOnly = true)
    public RoomResponse findCustomerRoom(String username) {
        return roomRepository.findByUserUsername(username).map(this::toCustomerRoomResponse).orElse(null);
    }

    @Transactional(readOnly = true)
    public List<MessageResponse> listMessages(Authentication authentication, Long roomId,
                                              Long beforeId, Long afterId, int size) {
        Room room = getRoom(roomId);
        boolean owner = isOwner(room, authentication);
        if (!owner && !isStaff(authentication)) {
            throw new AccessDeniedException("Không có quyền truy cập phòng chat này");
        }
        int pageSize = Math.max(1, Math.min(size, MAX_PAGE_SIZE));
        List<Message> messages = new ArrayList<>(messageRepository.findForRoom(
                roomId, beforeId, afterId, !owner, PageRequest.of(0, pageSize)));
        Collections.reverse(messages);
        return messageMapper.entityToResponse(messages);
    }

    @Transactional(readOnly = true)
    public List<RoomResponse> listInboxRooms() {
        Map<Long, Long> unreadByRoom = new HashMap<>();
        for (Object[] row : messageRepository.countAgentUnreadGroupByRoom()) {
            unreadByRoom.put((Long) row[0], (Long) row[1]);
        }
        List<RoomResponse> responses = new ArrayList<>();
        for (Room room : roomRepository.findAllForInbox()) {
            RoomResponse response = roomMapper.entityToResponse(room);
            response.setUnreadCount(unreadByRoom.getOrDefault(room.getId(), 0L));
            responses.add(response);
        }
        return responses;
    }

    // ================= Khách =================

    @Transactional
    public RoomResponse getOrCreateRoom(String username) {
        Room room = roomRepository.findByUserUsername(username).orElseGet(() -> {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new ResourceNotFoundException(ResourceName.USER, FieldName.USERNAME, username));
            Room newRoom = new Room();
            newRoom.setName(user.getFullname());
            newRoom.setUser(user);
            newRoom.setStatus(isBotActive() ? RoomStatus.BOT : RoomStatus.WAITING_AGENT);
            Room saved = roomRepository.save(newRoom);
            publishRoom(saved);
            return saved;
        });
        return toCustomerRoomResponse(room);
    }

    @Transactional
    public MessageResponse send(Authentication authentication, Long roomId, ChatSendRequest request) {
        String content = request.getContent() == null ? "" : request.getContent().trim();
        if (content.isEmpty()) {
            throw new IllegalArgumentException("Nội dung tin nhắn trống");
        }
        if (content.length() > MAX_CONTENT_LENGTH) {
            content = content.substring(0, MAX_CONTENT_LENGTH);
        }

        // Gửi lại cùng clientMsgId (mất mạng, retry) thì trả về bản ghi cũ, không lưu trùng
        if (request.getClientMsgId() != null) {
            Message existing = messageRepository.findByClientMsgId(request.getClientMsgId()).orElse(null);
            if (existing != null) {
                if (!Objects.equals(existing.getRoom().getId(), roomId)) {
                    throw new AccessDeniedException("clientMsgId không hợp lệ");
                }
                return messageMapper.entityToResponse(existing);
            }
        }

        Room room = getRoom(roomId);
        User sender = getUser(authentication.getName());
        boolean owner = room.getUser().getId().equals(sender.getId());
        if (!owner && !isStaff(authentication)) {
            throw new AccessDeniedException("Không có quyền gửi tin vào phòng chat này");
        }

        if (owner) {
            return sendAsCustomer(room, sender, content, request.getClientMsgId());
        }
        return sendAsAgent(room, sender, content, request.getClientMsgId(), null);
    }

    private MessageResponse sendAsCustomer(Room room, User customer, String content, String clientMsgId) {
        if (room.getStatus() == RoomStatus.RESOLVED) {
            // Mở phiên mới
            room.setAssignee(null);
            room.setStatus(isBotActive() ? RoomStatus.BOT : RoomStatus.WAITING_AGENT);
        } else if (room.getStatus() == RoomStatus.BOT && !isBotActive()) {
            room.setStatus(RoomStatus.WAITING_AGENT);
        }

        Message message = saveMessage(room, customer, MessageType.TEXT, SenderType.CUSTOMER, content, null, clientMsgId);
        room.setCustomerLastReadId(message.getId());

        if (room.getStatus() == RoomStatus.BOT) {
            if (wantsHuman(content)) {
                moveToWaitingAgent(room);
            } else {
                Long roomId = room.getId();
                Long messageId = message.getId();
                afterCommit(() -> eventPublisher.publishEvent(new CustomerMessageEvent(roomId, messageId)));
            }
        }

        roomRepository.save(room);
        publishRoom(room);
        return messageMapper.entityToResponse(message);
    }

    private MessageResponse sendAsAgent(Room room, User agent, String content, String clientMsgId, JsonNode payload) {
        boolean joined = assignIfNeeded(room, agent);
        Message message = saveMessage(room, agent, MessageType.TEXT, SenderType.AGENT, content, payload, clientMsgId);
        room.setAgentLastReadId(message.getId());
        roomRepository.save(room);
        publishRoom(room);
        if (joined) {
            notifyCustomer(room, agent.getFullname() + " đã phản hồi yêu cầu tư vấn của bạn");
        }
        return messageMapper.entityToResponse(message);
    }

    @Transactional
    public RoomResponse requestAgent(Authentication authentication) {
        Room room = roomRepository.findByUserUsername(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException(ResourceName.ROOM, FieldName.USERNAME, authentication.getName()));
        if (room.getStatus() == RoomStatus.BOT || room.getStatus() == RoomStatus.RESOLVED) {
            moveToWaitingAgent(room);
            roomRepository.save(room);
            publishRoom(room);
        }
        return toCustomerRoomResponse(room);
    }

    @Transactional
    public void markRead(Authentication authentication, Long roomId) {
        Room room = getRoom(roomId);
        Long lastId = room.getLastMessage() == null ? null : room.getLastMessage().getId();
        if (isOwner(room, authentication)) {
            room.setCustomerLastReadId(lastId);
            roomRepository.save(room);
        } else if (isStaff(authentication)) {
            room.setAgentLastReadId(lastId);
            roomRepository.save(room);
            publishRoom(room);
        } else {
            throw new AccessDeniedException("Không có quyền truy cập phòng chat này");
        }
    }

    // ================= Nhân viên =================

    @Transactional
    public RoomResponse claim(Authentication authentication, Long roomId) {
        Room room = getRoom(roomId);
        User agent = getUser(authentication.getName());
        if (assignIfNeeded(room, agent)) {
            notifyCustomer(room, agent.getFullname() + " đã tham gia hỗ trợ bạn");
        }
        roomRepository.save(room);
        publishRoom(room);
        return toAgentRoomResponse(room);
    }

    @Transactional
    public RoomResponse releaseToBot(Long roomId) {
        Room room = getRoom(roomId);
        room.setAssignee(null);
        if (isBotActive()) {
            room.setStatus(RoomStatus.BOT);
            saveSystemMessage(room, BOT_NAME + " sẽ tiếp tục hỗ trợ bạn. Bạn có thể yêu cầu gặp tư vấn viên bất cứ lúc nào.");
        } else {
            room.setStatus(RoomStatus.WAITING_AGENT);
        }
        roomRepository.save(room);
        publishRoom(room);
        return toAgentRoomResponse(room);
    }

    @Transactional
    public RoomResponse resolve(Authentication authentication, Long roomId) {
        Room room = getRoom(roomId);
        if (!isOwner(room, authentication) && !isStaff(authentication)) {
            throw new AccessDeniedException("Không có quyền truy cập phòng chat này");
        }
        if (room.getStatus() != RoomStatus.RESOLVED) {
            room.setStatus(RoomStatus.RESOLVED);
            room.setAssignee(null);
            saveSystemMessage(room, "Cuộc trò chuyện đã kết thúc. Cảm ơn bạn đã liên hệ Necom!");
            roomRepository.save(room);
            publishRoom(room);
        }
        return isStaff(authentication) ? toAgentRoomResponse(room) : toCustomerRoomResponse(room);
    }

    @Transactional
    public MessageResponse addInternalNote(Authentication authentication, Long roomId, String content) {
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Nội dung ghi chú trống");
        }
        Room room = getRoom(roomId);
        User agent = getUser(authentication.getName());
        // Không cập nhật room.lastMessage: lastMessage được trả cho khách, ghi chú nội bộ không được lộ ra
        Message note = saveMessage(room, agent, MessageType.INTERNAL_NOTE, SenderType.AGENT, content.trim(), null, null);
        return messageMapper.entityToResponse(note);
    }

    @Transactional
    public MessageResponse sendOrderCard(Authentication authentication, Long roomId, String orderCode) {
        Room room = getRoom(roomId);
        Order order = orderRepository.findByCode(orderCode)
                .filter(o -> o.getUser() != null && o.getUser().getId().equals(room.getUser().getId()))
                .orElseThrow(() -> new ResourceNotFoundException(ResourceName.ORDER, FieldName.CODE, orderCode));
        ObjectNode payload = objectMapper.createObjectNode();
        payload.putArray("orders").add(orderCard(order));
        User agent = getUser(authentication.getName());
        return sendAsAgent(room, agent, "Thông tin đơn hàng #" + order.getCode(), null, payload);
    }

    // ================= Bot =================

    /**
     * Lưu câu trả lời của bot. Bỏ qua nếu trong lúc bot đang nghĩ, phòng đã chuyển cho nhân viên.
     */
    @Transactional
    public void sendBotMessage(Long roomId, String content, JsonNode payload, boolean handoff) {
        Room room = getRoom(roomId);
        if (room.getStatus() != RoomStatus.BOT) {
            log.debug("Skip bot reply for room {} because status is {}", roomId, room.getStatus());
            return;
        }
        saveMessage(room, null, MessageType.TEXT, SenderType.BOT, content, payload, null);
        if (handoff) {
            moveToWaitingAgent(room);
        }
        roomRepository.save(room);
        publishRoom(room);
    }

    public void publishTyping(Long roomId, SenderType senderType, String name, boolean active, boolean toCustomer) {
        ChatEvent event = ChatEvent.typing(roomId, new ChatEvent.Typing(senderType.name(), name, active));
        if (toCustomer) {
            messagingTemplate.convertAndSend(ROOM_TOPIC_PREFIX + roomId, event);
        }
        messagingTemplate.convertAndSend(ADMIN_TOPIC, event);
    }

    public ObjectNode orderCard(Order order) {
        ObjectNode card = objectMapper.createObjectNode();
        card.put("id", order.getId());
        card.put("code", order.getCode());
        card.put("status", order.getStatus());
        card.put("statusLabel", orderStatusLabel(order.getStatus()));
        card.put("totalPay", order.getTotalPay());
        card.put("createdAt", order.getCreatedAt().toString());
        card.put("itemCount", order.getOrderVariants().size());
        return card;
    }

    public static String orderStatusLabel(Integer status) {
        if (status == null) {
            return "Không rõ";
        }
        switch (status) {
            case 1:
                return "Đơn hàng mới";
            case 2:
                return "Đang xử lý";
            case 3:
                return "Đang giao hàng";
            case 4:
                return "Đã giao hàng";
            case 5:
                return "Đã hủy";
            default:
                return "Không rõ";
        }
    }

    // ================= Nội bộ =================

    static boolean wantsHuman(String content) {
        String normalized = VietnameseTextUtils.normalize(content);
        return HANDOFF_PHRASES.stream().anyMatch(phrase -> VietnameseTextUtils.containsWord(normalized, phrase));
    }

    private void moveToWaitingAgent(Room room) {
        room.setStatus(RoomStatus.WAITING_AGENT);
        room.setAssignee(null);
        ObjectNode payload = objectMapper.createObjectNode();
        payload.put("event", "WAITING_AGENT");
        saveMessage(room, null, MessageType.SYSTEM, SenderType.SYSTEM,
                "Đang kết nối bạn với tư vấn viên, vui lòng chờ trong giây lát. Bạn có thể tiếp tục nhắn, tư vấn viên sẽ đọc toàn bộ cuộc trò chuyện.",
                payload, null);
    }

    /**
     * @return true nếu nhân viên vừa được gán vào phòng (tạo tin hệ thống "... đã tham gia")
     */
    private boolean assignIfNeeded(Room room, User agent) {
        boolean alreadyAssigned = room.getStatus() == RoomStatus.AGENT
                && room.getAssignee() != null
                && room.getAssignee().getId().equals(agent.getId());
        if (alreadyAssigned) {
            return false;
        }
        room.setStatus(RoomStatus.AGENT);
        room.setAssignee(agent);
        ObjectNode payload = objectMapper.createObjectNode();
        payload.put("event", "AGENT_JOINED");
        payload.put("agentName", agent.getFullname());
        saveMessage(room, null, MessageType.SYSTEM, SenderType.SYSTEM,
                agent.getFullname() + " – Tư vấn viên đã tham gia cuộc trò chuyện", payload, null);
        return true;
    }

    private void saveSystemMessage(Room room, String content) {
        saveMessage(room, null, MessageType.SYSTEM, SenderType.SYSTEM, content, null, null);
    }

    private Message saveMessage(Room room, User user, MessageType type, SenderType senderType,
                                String content, JsonNode payload, String clientMsgId) {
        Message message = new Message();
        message.setRoom(room);
        message.setUser(user);
        message.setType(type);
        message.setSenderType(senderType);
        message.setContent(content);
        message.setPayload(payload);
        message.setClientMsgId(clientMsgId);
        message.setStatus(1);
        Message saved = messageRepository.save(message);

        if (type != MessageType.INTERNAL_NOTE) {
            room.setLastMessage(saved);
            room.setUpdatedAt(Instant.now());
        }

        MessageResponse response = messageMapper.entityToResponse(saved);
        afterCommit(() -> {
            ChatEvent event = ChatEvent.message(response);
            if (type != MessageType.INTERNAL_NOTE) {
                messagingTemplate.convertAndSend(ROOM_TOPIC_PREFIX + room.getId(), event);
            }
            messagingTemplate.convertAndSend(ADMIN_TOPIC, event);
        });
        return saved;
    }

    private void publishRoom(Room room) {
        RoomResponse customerView = toCustomerRoomResponse(room);
        RoomResponse agentView = toAgentRoomResponse(room);
        afterCommit(() -> {
            messagingTemplate.convertAndSend(ROOM_TOPIC_PREFIX + room.getId(), ChatEvent.room(customerView));
            messagingTemplate.convertAndSend(ADMIN_TOPIC, ChatEvent.room(agentView));
        });
    }

    private void notifyCustomer(Room room, String text) {
        Notification notification = new Notification();
        notification.setUser(room.getUser());
        notification.setType(NotificationType.CHAT);
        notification.setMessage(text);
        notification.setAnchor("/user/chat");
        notification.setStatus(1);
        Notification saved = notificationRepository.save(notification);
        NotificationResponse response = notificationMapper.entityToResponse(saved);
        String username = room.getUser().getUsername();
        afterCommit(() -> notificationService.pushNotification(username, response));
    }

    private RoomResponse toCustomerRoomResponse(Room room) {
        RoomResponse response = roomMapper.entityToResponse(room);
        long lastRead = room.getCustomerLastReadId() == null ? 0L : room.getCustomerLastReadId();
        response.setUnreadCount(room.getId() == null ? 0L
                : messageRepository.countUnread(room.getId(), lastRead, CUSTOMER_UNREAD_SENDERS));
        return response;
    }

    private RoomResponse toAgentRoomResponse(Room room) {
        RoomResponse response = roomMapper.entityToResponse(room);
        long lastRead = room.getAgentLastReadId() == null ? 0L : room.getAgentLastReadId();
        response.setUnreadCount(room.getId() == null ? 0L
                : messageRepository.countUnread(room.getId(), lastRead, EnumSet.of(SenderType.CUSTOMER)));
        return response;
    }

    private boolean isOwner(Room room, Authentication authentication) {
        return room.getUser().getUsername().equals(authentication.getName());
    }

    private Room getRoom(Long roomId) {
        return roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceName.ROOM, FieldName.ID, roomId));
    }

    private User getUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceName.USER, FieldName.USERNAME, username));
    }

    /**
     * Broadcast/phát sự kiện chỉ sau khi transaction commit, tránh client nhận tin rồi đọc DB không thấy.
     */
    private static void afterCommit(Runnable action) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    action.run();
                }
            });
        } else {
            action.run();
        }
    }

    // Dùng cho payload danh sách chip gợi ý
    public ArrayNode quickReplies(List<String> options) {
        ArrayNode array = objectMapper.createArrayNode();
        options.forEach(array::add);
        return array;
    }

}
