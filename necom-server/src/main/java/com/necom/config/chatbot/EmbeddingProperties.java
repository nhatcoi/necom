package com.necom.config.chatbot;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Dịch vụ embedding (Hugging Face text-embeddings-inference, model multilingual-e5-small).
 * Để trống base-url thì tìm kiếm chỉ dùng từ khóa.
 */
@Component
@ConfigurationProperties(prefix = "necom.embedding")
@Getter
@Setter
public class EmbeddingProperties {
    private String baseUrl;
    private String model = "intfloat/multilingual-e5-small";
    private int batchSize = 32;
    private int timeoutSeconds = 30;

    public boolean isActive() {
        return StringUtils.hasText(baseUrl);
    }
}
