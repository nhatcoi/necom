package com.necom.config.websocket;

import com.necom.config.security.JwtUtils;
import com.necom.config.security.UserDetailsServiceImpl;
import com.necom.repository.chat.RoomRepository;
import com.necom.service.chat.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Xác thực JWT ở frame CONNECT (header Authorization: Bearer ...) và kiểm tra quyền ở frame SUBSCRIBE:
 * - /chat/receive/admin: chỉ ADMIN/EMPLOYEE
 * - /chat/receive/{roomId}: chủ phòng hoặc ADMIN/EMPLOYEE
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class StompAuthChannelInterceptor implements ChannelInterceptor {

    private final JwtUtils jwtUtils;
    private final UserDetailsServiceImpl userDetailsService;
    // Không inject ChatService: ChatService cần SimpMessagingTemplate, tạo vòng phụ thuộc với WebsocketConfig
    private final RoomRepository roomRepository;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null || accessor.getCommand() == null) {
            return message;
        }

        StompCommand command = accessor.getCommand();
        if (StompCommand.CONNECT.equals(command)) {
            accessor.setUser(authenticate(accessor.getFirstNativeHeader("Authorization")));
        } else if (StompCommand.SUBSCRIBE.equals(command)) {
            authorizeSubscribe((Authentication) accessor.getUser(), accessor.getDestination());
        } else if (StompCommand.SEND.equals(command) && accessor.getUser() == null) {
            throw new MessagingException("Unauthorized");
        }
        return message;
    }

    private Authentication authenticate(String header) {
        if (!StringUtils.hasText(header) || !header.startsWith("Bearer ")) {
            throw new MessagingException("Missing token");
        }
        String token = header.substring(7);
        if (!jwtUtils.validateJwtToken(token)) {
            throw new MessagingException("Invalid token");
        }
        UserDetails userDetails = userDetailsService.loadUserByUsername(jwtUtils.getUsernameFromJwt(token));
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    private void authorizeSubscribe(Authentication authentication, String destination) {
        if (authentication == null || destination == null) {
            throw new MessagingException("Unauthorized");
        }
        if (destination.equals(ChatService.ADMIN_TOPIC)) {
            if (!ChatService.isStaff(authentication)) {
                throw new MessagingException("Forbidden");
            }
            return;
        }
        if (destination.startsWith(ChatService.ROOM_TOPIC_PREFIX)) {
            Long roomId;
            try {
                roomId = Long.valueOf(destination.substring(ChatService.ROOM_TOPIC_PREFIX.length()));
            } catch (NumberFormatException e) {
                throw new MessagingException("Forbidden");
            }
            if (!ChatService.isStaff(authentication)
                    && !roomRepository.existsByIdAndUserUsername(roomId, authentication.getName())) {
                log.warn("User {} tried to subscribe room {}", authentication.getName(), roomId);
                throw new MessagingException("Forbidden");
            }
            return;
        }
        throw new MessagingException("Forbidden");
    }

}
