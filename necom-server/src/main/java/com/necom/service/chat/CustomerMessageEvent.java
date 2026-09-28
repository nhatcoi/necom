package com.necom.service.chat;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Phát ra khi khách gửi tin vào phòng đang ở trạng thái BOT, chatbot lắng nghe sau khi transaction commit.
 */
@Getter
@AllArgsConstructor
public class CustomerMessageEvent {
    private final Long roomId;
    private final Long messageId;
}
