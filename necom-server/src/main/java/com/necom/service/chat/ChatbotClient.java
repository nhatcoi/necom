package com.necom.service.chat;

import com.fasterxml.jackson.databind.JsonNode;
import com.necom.config.chatbot.ChatbotProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Gọi endpoint /chat/completions theo chuẩn OpenAI (proxy nội bộ, Gemini, OpenAI... đều dùng được).
 */
@Component
@Slf4j
public class ChatbotClient {

    private final ChatbotProperties properties;
    private final WebClient webClient;

    public ChatbotClient(ChatbotProperties properties, WebClient.Builder webClientBuilder) {
        this.properties = properties;
        this.webClient = webClientBuilder.build();
    }

    public String complete(List<Map<String, String>> messages) {
        Map<String, Object> body = new HashMap<>();
        body.put("model", properties.getModel());
        body.put("messages", messages);
        body.put("temperature", properties.getTemperature());

        String baseUrl = properties.getBaseUrl().replaceAll("/+$", "");
        JsonNode response = webClient.post()
                .uri(baseUrl + "/chat/completions")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + properties.getApiKey())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block(Duration.ofSeconds(properties.getTimeoutSeconds()));

        if (response == null) {
            throw new IllegalStateException("Chatbot API trả về rỗng");
        }
        return response.path("choices").path(0).path("message").path("content").asText("");
    }

    /**
     * Một lượt hội thoại có tool calling (chuẩn OpenAI). Trả về message của assistant: có content hoặc tool_calls.
     */
    public JsonNode chat(List<? extends Object> messages, JsonNode tools, double temperature) {
        Map<String, Object> body = new HashMap<>();
        body.put("model", properties.getModel());
        body.put("messages", messages);
        body.put("temperature", temperature);
        if (tools != null && tools.size() > 0) {
            body.put("tools", tools);
            body.put("tool_choice", "auto");
        }
        String baseUrl = properties.getBaseUrl().replaceAll("/+$", "");
        JsonNode response = webClient.post()
                .uri(baseUrl + "/chat/completions")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + properties.getApiKey())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block(Duration.ofSeconds(properties.getTimeoutSeconds()));
        if (response == null) {
            throw new IllegalStateException("Chatbot API trả về rỗng");
        }
        return response.path("choices").path(0).path("message");
    }

    public static Map<String, String> message(String role, String content) {
        Map<String, String> message = new HashMap<>();
        message.put("role", role);
        message.put("content", content);
        return message;
    }

}
