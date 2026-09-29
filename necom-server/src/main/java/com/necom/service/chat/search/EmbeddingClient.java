package com.necom.service.chat.search;

import com.fasterxml.jackson.databind.JsonNode;
import com.necom.config.chatbot.EmbeddingProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Gọi TEI /embed. Model họ e5 cần tiền tố "query: " cho câu hỏi và "passage: " cho tài liệu.
 * Vector trả về đã chuẩn hóa (normalize=true) nên cosine = tích vô hướng.
 */
@Component
@Slf4j
public class EmbeddingClient {

    private final EmbeddingProperties properties;
    private final WebClient webClient;

    public EmbeddingClient(EmbeddingProperties properties, WebClient.Builder builder) {
        this.properties = properties;
        this.webClient = builder.codecs(c -> c.defaultCodecs().maxInMemorySize(16 * 1024 * 1024)).build();
    }

    public boolean isActive() {
        return properties.isActive();
    }

    public String model() {
        return properties.getModel();
    }

    public float[] embedQuery(String text) {
        return embed(List.of("query: " + text)).get(0);
    }

    public List<float[]> embedPassages(List<String> texts) {
        List<float[]> result = new ArrayList<>();
        int batch = Math.max(1, properties.getBatchSize());
        for (int i = 0; i < texts.size(); i += batch) {
            List<String> chunk = new ArrayList<>();
            for (String text : texts.subList(i, Math.min(texts.size(), i + batch))) {
                chunk.add("passage: " + text);
            }
            result.addAll(embed(chunk));
        }
        return result;
    }

    private List<float[]> embed(List<String> inputs) {
        JsonNode response = webClient.post()
                .uri(properties.getBaseUrl().replaceAll("/+$", "") + "/embed")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("inputs", inputs, "normalize", true, "truncate", true))
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block(Duration.ofSeconds(properties.getTimeoutSeconds()));
        List<float[]> vectors = new ArrayList<>();
        if (response == null || !response.isArray()) {
            throw new IllegalStateException("TEI trả về dữ liệu không hợp lệ");
        }
        for (JsonNode row : response) {
            float[] vector = new float[row.size()];
            for (int i = 0; i < row.size(); i++) {
                vector[i] = (float) row.get(i).asDouble();
            }
            vectors.add(vector);
        }
        return vectors;
    }

    public static float dot(float[] a, float[] b) {
        float sum = 0;
        int n = Math.min(a.length, b.length);
        for (int i = 0; i < n; i++) {
            sum += a[i] * b[i];
        }
        return sum;
    }

}
