package com.necom.dto.chat;

import com.necom.entity.chat.RoomStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomResponse {
    private Long id;
    private Instant createdAt;
    private Instant updatedAt;
    private String name;
    private UserResponse user;
    private MessageResponse lastMessage;
    private RoomStatus status;
    private UserResponse assignee;
    // Số tin chưa đọc theo góc nhìn người gọi API (khách hoặc nhân viên), tính riêng ngoài mapper
    private Long unreadCount;

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
