package com.necom.config.chatbot;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Cấu hình trợ lý AI (API tương thích OpenAI). Mọi giá trị nhạy cảm lấy từ biến môi trường,
 * xem necom.chatbot trong application.yml.
 */
@Component
@ConfigurationProperties(prefix = "necom.chatbot")
@Getter
@Setter
public class ChatbotProperties {
    private boolean enabled = true;
    // Ví dụ: https://host/v1 (không có dấu / ở cuối)
    private String baseUrl;
    private String apiKey;
    private String model;
    private int timeoutSeconds = 45;
    // Số tin gần nhất đưa vào ngữ cảnh hội thoại
    private int historySize = 12;
    private double temperature = 0.4;

    public boolean isActive() {
        return enabled && StringUtils.hasText(baseUrl) && StringUtils.hasText(apiKey) && StringUtils.hasText(model);
    }
}
