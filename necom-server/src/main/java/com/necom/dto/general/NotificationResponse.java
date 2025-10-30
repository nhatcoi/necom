package com.necom.dto.general;

import com.necom.entity.general.NotificationType;
import lombok.Data;
import org.springframework.lang.Nullable;

import java.time.Instant;

@Data
public class NotificationResponse {
    private Long id;
    private Instant createdAt;
    private NotificationType type;
    private String message;
    @Nullable
    private String anchor;
    private Integer status;
}
