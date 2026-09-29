package com.necom.service.chat.search;

import com.necom.utils.VietnameseTextUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Kiến thức cửa hàng (chatbot/knowledge.md) cắt theo mục "## ", truy xuất top-k theo embedding hoặc từ khóa.
 * Bot chỉ nhận những mục liên quan thay vì toàn bộ tài liệu, nên kho kiến thức có thể lớn dần mà prompt không phình.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class KnowledgeIndex {

    private final EmbeddingClient embeddingClient;

    private volatile List<Chunk> chunks;
    private volatile Map<Integer, float[]> vectors = Map.of();

    public List<Chunk> search(String query, int limit) {
        List<Chunk> all = load();
        if (query == null || query.isBlank()) {
            return all.stream().limit(limit).collect(Collectors.toList());
        }
        Map<Integer, Double> scores = new HashMap<>();
        List<String> terms = ProductSearchIndex.queryTerms(query);
        for (Chunk chunk : all) {
            double lexical = 0;
            for (String term : terms) {
                if (VietnameseTextUtils.containsWord(chunk.getNormalized(), term)) {
                    lexical += VietnameseTextUtils.containsWord(VietnameseTextUtils.normalize(chunk.getTitle()), term) ? 2 : 1;
                }
            }
            scores.put(chunk.getIndex(), lexical / Math.max(1, terms.size()));
        }
        if (embeddingClient.isActive()) {
            try {
                ensureVectors(all);
                float[] q = embeddingClient.embedQuery(query);
                for (Chunk chunk : all) {
                    float[] v = vectors.get(chunk.getIndex());
                    if (v != null) {
                        scores.merge(chunk.getIndex(), (double) EmbeddingClient.dot(q, v) * 3, Double::sum);
                    }
                }
            } catch (Exception e) {
                log.warn("Knowledge semantic search failed: {}", e.getMessage());
            }
        }
        return all.stream()
                .sorted(Comparator.comparingDouble((Chunk c) -> -scores.getOrDefault(c.getIndex(), 0.0)))
                .limit(limit)
                .collect(Collectors.toList());
    }

    public String overview() {
        List<Chunk> all = load();
        return all.isEmpty() ? "" : all.get(0).getContent();
    }

    private synchronized void ensureVectors(List<Chunk> all) {
        if (!vectors.isEmpty()) {
            return;
        }
        List<float[]> embedded = embeddingClient.embedPassages(all.stream().map(c -> c.getTitle() + ". " + c.getContent()).collect(Collectors.toList()));
        Map<Integer, float[]> map = new HashMap<>();
        for (int i = 0; i < all.size(); i++) {
            map.put(all.get(i).getIndex(), embedded.get(i));
        }
        vectors = map;
    }

    private List<Chunk> load() {
        if (chunks != null) {
            return chunks;
        }
        synchronized (this) {
            if (chunks != null) {
                return chunks;
            }
            List<Chunk> result = new ArrayList<>();
            try {
                String markdown = StreamUtils.copyToString(new ClassPathResource("chatbot/knowledge.md").getInputStream(), StandardCharsets.UTF_8);
                String title = "Giới thiệu";
                StringBuilder body = new StringBuilder();
                for (String line : markdown.split("\n")) {
                    if (line.startsWith("## ")) {
                        addChunk(result, title, body);
                        title = line.substring(3).trim();
                        body = new StringBuilder();
                    } else if (!line.startsWith("# ")) {
                        body.append(line).append('\n');
                    }
                }
                addChunk(result, title, body);
            } catch (Exception e) {
                log.warn("Cannot load chatbot knowledge: {}", e.getMessage());
            }
            chunks = result;
            return chunks;
        }
    }

    private static void addChunk(List<Chunk> result, String title, StringBuilder body) {
        String content = body.toString().trim();
        if (!content.isEmpty()) {
            result.add(new Chunk(result.size(), title, content, VietnameseTextUtils.normalize(title + " " + content)));
        }
    }

    @Getter
    @RequiredArgsConstructor
    public static class Chunk {
        private final int index;
        private final String title;
        private final String content;
        private final String normalized;
    }

}
