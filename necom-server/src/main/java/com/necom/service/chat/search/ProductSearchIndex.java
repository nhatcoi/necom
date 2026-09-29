package com.necom.service.chat.search;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.necom.projection.inventory.SimpleProductInventory;
import com.necom.repository.ProjectionRepository;
import com.necom.utils.VietnameseTextUtils;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HexFormat;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Chỉ mục tìm kiếm sản phẩm cho trợ lý AI: BM25 trên văn bản đã bỏ dấu + tìm ngữ nghĩa bằng embedding (TEI),
 * gộp bằng Reciprocal Rank Fusion, lọc cứng theo danh mục / giá / còn hàng.
 * <p>
 * Toàn bộ chỉ mục nằm trong bộ nhớ (mỗi sản phẩm vài KB + vector 384 chiều ≈ 1,5 KB): phù hợp tới vài chục nghìn
 * sản phẩm. Khi lớn hơn, thay phần vector bằng vector store (Qdrant) mà không đổi giao diện search().
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ProductSearchIndex {

    private static final int RRF_K = 60;
    private static final int SEMANTIC_CANDIDATES = 40;
    private static final double BM25_K1 = 1.2;
    private static final double BM25_B = 0.75;

    static final Set<String> STOPWORDS = Set.of(
            "toi", "minh", "ban", "em", "anh", "chi", "shop", "oi", "a", "ah", "nhe", "nha", "voi", "va", "hoac",
            "muon", "can", "co", "khong", "ko", "k", "cho", "la", "cua", "nao", "gi", "the", "nay", "do", "mot",
            "nhung", "cac", "duoc", "hoi", "xem", "tim", "mua", "gia", "bao", "nhieu", "vay", "sao", "lam", "nhu",
            "thi", "se", "da", "dang", "rat", "qua", "hay", "hon", "den", "trong", "tren", "duoi", "ve",
            "nen", "giup", "van", "san", "pham", "loai", "mau", "kieu", "dep", "re", "tot", "hang", "con",
            "khoang", "tam", "trieu", "tr", "nghin", "ngan", "dong", "vnd", "d", "goi", "y", "biet", "them",
            "thich", "phu", "hop", "de", "o", "nhat", "moi", "ai", "cai", "chiec", "bo", "ne", "di", "roi"
    );

    // "dưới 5 triệu", "tối đa 500k", "trên 2tr". Không dùng "từ" vì bỏ dấu trùng với "tủ"
    private static final Pattern PRICE_PATTERN = Pattern.compile(
            "\\b(duoi|toi da|khong qua|re hon|tren|hon)\\s*(\\d+(?:[ .,]\\d+)?)\\s*(trieu|tr|k|nghin|ngan|d|dong)?\\b");

    private static final String LOAD_SQL = "SELECT p.id, p.name, p.slug, p.short_description, p.description, p.properties, "
            + "p.specifications, p.updated_at, c.name AS category, c.slug AS category_slug, b.name AS brand, "
            + "(SELECT MIN(v.price) FROM variant v WHERE v.product_id = p.id AND v.status = 1) AS min_price, "
            + "(SELECT MAX(v.price) FROM variant v WHERE v.product_id = p.id AND v.status = 1) AS max_price, "
            + "(SELECT i.path FROM image i WHERE i.product_id = p.id ORDER BY i.is_thumbnail DESC, i.id LIMIT 1) AS thumbnail, "
            + "(SELECT AVG(r.rating_score) FROM review r WHERE r.product_id = p.id) AS rating, "
            + "(SELECT COUNT(*) FROM review r WHERE r.product_id = p.id) AS review_count, "
            + "(SELECT GROUP_CONCAT(t.name SEPARATOR ', ') FROM product_tag pt JOIN tag t ON t.id = pt.tag_id WHERE pt.product_id = p.id) AS tags "
            + "FROM product p LEFT JOIN category c ON c.id = p.category_id LEFT JOIN brand b ON b.id = p.brand_id "
            + "WHERE p.status = 1";

    private final JdbcTemplate jdbcTemplate;
    private final ProjectionRepository projectionRepository;
    private final EmbeddingClient embeddingClient;
    private final ObjectMapper objectMapper;

    private volatile Snapshot snapshot = Snapshot.empty();

    // ================= Truy vấn =================

    public SearchResult search(SearchQuery query) {
        Snapshot s = ensureLoaded();
        String text = query.getText() == null ? "" : query.getText().trim();

        PriceRange range = PriceRange.of(query.getMinPrice(), query.getMaxPrice());
        if (range.isEmpty()) {
            range = parsePriceRange(VietnameseTextUtils.removeAccents(text));
        }
        Set<String> categories = query.getCategorySlugs() == null ? Set.of() : Set.copyOf(query.getCategorySlugs());

        List<ProductDoc> candidates = new ArrayList<>();
        for (ProductDoc doc : s.docs) {
            if (!categories.isEmpty() && !categories.contains(doc.getCategorySlug())) {
                continue;
            }
            if (query.isInStockOnly() && doc.getStock() <= 0) {
                continue;
            }
            if (!range.overlaps(doc.getMinPrice(), doc.getMaxPrice())) {
                continue;
            }
            candidates.add(doc);
        }

        Map<Long, Double> fused = new HashMap<>();
        Map<Long, Double> semanticScores = new HashMap<>();
        boolean semanticUsed = false;
        if (!text.isEmpty()) {
            List<ProductDoc> lexical = lexicalRank(s, candidates, text);
            for (int i = 0; i < lexical.size(); i++) {
                fused.merge(lexical.get(i).getId(), 1.0 / (RRF_K + i + 1), Double::sum);
            }
            float[] queryVector = embedQuerySafely(text);
            if (queryVector != null && !s.vectors.isEmpty()) {
                semanticUsed = true;
                List<ProductDoc> semantic = candidates.stream()
                        .filter(d -> s.vectors.containsKey(d.getId()))
                        .sorted(Comparator.comparingDouble((ProductDoc d) -> -EmbeddingClient.dot(queryVector, s.vectors.get(d.getId()))))
                        .limit(SEMANTIC_CANDIDATES)
                        .collect(Collectors.toList());
                for (int i = 0; i < semantic.size(); i++) {
                    ProductDoc d = semantic.get(i);
                    semanticScores.put(d.getId(), (double) EmbeddingClient.dot(queryVector, s.vectors.get(d.getId())));
                    fused.merge(d.getId(), 1.0 / (RRF_K + i + 1), Double::sum);
                }
            }
        } else {
            candidates.forEach(d -> fused.put(d.getId(), d.getRating() * 0.01 + Math.min(d.getReviewCount(), 50) * 0.0001));
        }

        Map<Long, ProductDoc> byId = s.byId;
        Comparator<ProductDoc> order = Comparator.comparingDouble((ProductDoc d) -> -fused.getOrDefault(d.getId(), 0.0));
        List<ProductDoc> ranked = fused.keySet().stream().map(byId::get).filter(Objects::nonNull).sorted(order)
                .limit(Math.max(query.getLimit() * 3L, 20))
                .collect(Collectors.toList());

        if ("price_asc".equals(query.getSort())) {
            ranked.sort(Comparator.comparing(ProductDoc::getMinPrice, Comparator.nullsLast(Comparator.naturalOrder())));
        } else if ("price_desc".equals(query.getSort())) {
            ranked.sort(Comparator.comparing(ProductDoc::getMinPrice, Comparator.nullsLast(Comparator.reverseOrder())));
        } else if ("rating".equals(query.getSort())) {
            ranked.sort(Comparator.comparingDouble((ProductDoc d) -> -d.getRating()));
        }

        List<ProductDoc> top = ranked.stream().limit(Math.max(1, query.getLimit())).collect(Collectors.toList());
        return new SearchResult(top, candidates.size(), semanticUsed, range, semanticScores);
    }

    public List<ProductDoc> similar(Long productId, int limit) {
        Snapshot s = ensureLoaded();
        ProductDoc base = s.byId.get(productId);
        if (base == null) {
            return List.of();
        }
        float[] vector = s.vectors.get(productId);
        Comparator<ProductDoc> order = vector != null
                ? Comparator.comparingDouble((ProductDoc d) -> -(EmbeddingClient.dot(vector, s.vectors.getOrDefault(d.getId(), new float[0]))
                + (Objects.equals(d.getCategorySlug(), base.getCategorySlug()) ? 0.05 : 0)))
                : Comparator.comparingDouble((ProductDoc d) -> Objects.equals(d.getCategorySlug(), base.getCategorySlug()) ? 0 : 1);
        return s.docs.stream()
                .filter(d -> !d.getId().equals(productId))
                .sorted(order)
                .limit(limit)
                .collect(Collectors.toList());
    }

    public ProductDoc get(Long id) {
        return ensureLoaded().byId.get(id);
    }

    public List<ProductDoc> getAll(Collection<Long> ids) {
        Snapshot s = ensureLoaded();
        return ids.stream().map(s.byId::get).filter(Objects::nonNull).collect(Collectors.toList());
    }

    public Map<String, String> categories() {
        Map<String, String> result = new java.util.LinkedHashMap<>();
        ensureLoaded().docs.forEach(d -> {
            if (d.getCategorySlug() != null) {
                result.putIfAbsent(d.getCategorySlug(), d.getCategory());
            }
        });
        return result;
    }

    public IndexStats stats() {
        Snapshot s = snapshot;
        return new IndexStats(s.docs.size(), s.vectors.size(), s.loadedAt, embeddingClient.isActive());
    }

    // ================= Làm mới chỉ mục (luồng nền) =================

    @Scheduled(initialDelayString = "PT20S", fixedDelayString = "PT5M")
    public void scheduledRefresh() {
        try {
            refresh();
        } catch (Exception e) {
            log.warn("Product search index refresh failed: {}", e.getMessage());
        }
    }

    public synchronized void refresh() {
        long started = System.currentTimeMillis();
        List<ProductDoc> docs = jdbcTemplate.query(LOAD_SQL, (rs, i) -> {
            Timestamp updated = rs.getTimestamp("updated_at");
            return ProductDoc.builder()
                    .id(rs.getLong("id"))
                    .name(rs.getString("name"))
                    .slug(rs.getString("slug"))
                    .shortDescription(rs.getString("short_description"))
                    .description(rs.getString("description"))
                    .attributes(extractAttributes(rs.getString("properties"), rs.getString("specifications")))
                    .category(rs.getString("category"))
                    .categorySlug(rs.getString("category_slug"))
                    .brand(rs.getString("brand"))
                    .minPrice(rs.getObject("min_price") == null ? null : rs.getDouble("min_price"))
                    .maxPrice(rs.getObject("max_price") == null ? null : rs.getDouble("max_price"))
                    .thumbnail(rs.getString("thumbnail"))
                    .rating(rs.getObject("rating") == null ? 0 : rs.getDouble("rating"))
                    .reviewCount(rs.getInt("review_count"))
                    .tags(rs.getString("tags"))
                    .updatedAt(updated == null ? Instant.EPOCH : updated.toInstant())
                    .build();
        });

        Map<Long, Integer> stock = new HashMap<>();
        List<Long> ids = docs.stream().map(ProductDoc::getId).collect(Collectors.toList());
        for (int i = 0; i < ids.size(); i += 500) {
            for (SimpleProductInventory inventory : projectionRepository.findSimpleProductInventories(ids.subList(i, Math.min(ids.size(), i + 500)))) {
                stock.put(inventory.getProductId(), inventory.getCanBeSold());
            }
        }
        docs.forEach(d -> d.setStock(stock.getOrDefault(d.getId(), 0)));
        docs.forEach(d -> {
            d.setTerms(weightedTerms(d));
            d.setTermLength(d.getTerms().values().stream().mapToInt(Integer::intValue).sum());
        });

        Map<Long, float[]> vectors = syncEmbeddings(docs);
        snapshot = Snapshot.build(docs, vectors);
        log.info("Product search index: {} products, {} vectors in {} ms", docs.size(), vectors.size(),
                System.currentTimeMillis() - started);
    }

    private Snapshot ensureLoaded() {
        if (snapshot.loadedAt == null) {
            refresh();
        }
        return snapshot;
    }

    /**
     * Chỉ tính embedding cho sản phẩm mới hoặc đổi nội dung (so content_hash), lưu vào product_embedding.
     */
    private Map<Long, float[]> syncEmbeddings(List<ProductDoc> docs) {
        Map<Long, float[]> vectors = new HashMap<>();
        Map<Long, String> storedHash = new HashMap<>();
        String model = embeddingClient.model();
        jdbcTemplate.query("SELECT product_id, content_hash, vector FROM product_embedding WHERE model = ?", rs -> {
            long id = rs.getLong("product_id");
            storedHash.put(id, rs.getString("content_hash"));
            vectors.put(id, fromBytes(rs.getBytes("vector")));
        }, model);

        if (!embeddingClient.isActive()) {
            return vectors;
        }
        List<ProductDoc> stale = new ArrayList<>();
        Map<Long, String> hashes = new HashMap<>();
        for (ProductDoc doc : docs) {
            String hash = sha256(embeddingText(doc));
            hashes.put(doc.getId(), hash);
            if (!hash.equals(storedHash.get(doc.getId()))) {
                stale.add(doc);
            }
        }
        if (stale.isEmpty()) {
            return vectors;
        }
        try {
            List<float[]> embedded = embeddingClient.embedPassages(stale.stream().map(ProductSearchIndex::embeddingText).collect(Collectors.toList()));
            List<Object[]> rows = new ArrayList<>();
            Timestamp now = Timestamp.from(Instant.now());
            for (int i = 0; i < stale.size(); i++) {
                ProductDoc doc = stale.get(i);
                float[] vector = embedded.get(i);
                vectors.put(doc.getId(), vector);
                rows.add(new Object[]{doc.getId(), model, hashes.get(doc.getId()), vector.length, toBytes(vector), now});
            }
            jdbcTemplate.batchUpdate("INSERT INTO product_embedding (product_id, model, content_hash, dimensions, vector, updated_at) "
                    + "VALUES (?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE model = VALUES(model), content_hash = VALUES(content_hash), "
                    + "dimensions = VALUES(dimensions), vector = VALUES(vector), updated_at = VALUES(updated_at)", rows);
            log.info("Embedded {} products with {}", stale.size(), model);
        } catch (Exception e) {
            log.warn("Cannot embed products (semantic search uses stored vectors only): {}", e.getMessage());
        }
        return vectors;
    }

    private float[] embedQuerySafely(String text) {
        if (!embeddingClient.isActive()) {
            return null;
        }
        try {
            return embeddingClient.embedQuery(text);
        } catch (Exception e) {
            log.warn("Query embedding failed, lexical only: {}", e.getMessage());
            return null;
        }
    }

    // ================= BM25 =================

    private List<ProductDoc> lexicalRank(Snapshot s, List<ProductDoc> candidates, String text) {
        List<String> queryTerms = queryTerms(text);
        if (queryTerms.isEmpty()) {
            return List.of();
        }
        String normalizedQuery = VietnameseTextUtils.normalize(text);
        Map<ProductDoc, Double> scores = new HashMap<>();
        for (ProductDoc doc : candidates) {
            double score = 0;
            for (String term : queryTerms) {
                Integer tf = doc.getTerms().get(term);
                if (tf == null) {
                    continue;
                }
                double idf = s.idf.getOrDefault(term, 0.0);
                double norm = BM25_K1 * (1 - BM25_B + BM25_B * doc.getTermLength() / s.avgLength);
                score += idf * (tf * (BM25_K1 + 1)) / (tf + norm);
            }
            // Cụm 2 từ liên tiếp khớp tên ("ghe an", "sofa goc") được cộng thêm
            for (int i = 0; i + 1 < queryTerms.size(); i++) {
                if (VietnameseTextUtils.containsWord(doc.getNormName(), queryTerms.get(i) + " " + queryTerms.get(i + 1))) {
                    score += 1.5;
                }
            }
            if (!normalizedQuery.isEmpty() && doc.getNormName().contains(normalizedQuery)) {
                score += 2;
            }
            if (score > 0) {
                scores.put(doc, score);
            }
        }
        return scores.entrySet().stream()
                .sorted(Map.Entry.<ProductDoc, Double>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    static List<String> queryTerms(String text) {
        return VietnameseTextUtils.tokenize(text).stream()
                .filter(t -> t.length() >= 2 && !STOPWORDS.contains(t) && !t.chars().allMatch(Character::isDigit))
                .distinct()
                .collect(Collectors.toList());
    }

    private static Map<String, Integer> weightedTerms(ProductDoc d) {
        Map<String, Integer> terms = new HashMap<>();
        addTerms(terms, d.getName(), 3);
        addTerms(terms, d.getCategory(), 2);
        addTerms(terms, d.getBrand(), 2);
        addTerms(terms, d.getTags(), 2);
        addTerms(terms, d.getAttributes(), 2);
        addTerms(terms, d.getShortDescription(), 1);
        addTerms(terms, d.getDescription(), 1);
        return terms;
    }

    private static void addTerms(Map<String, Integer> terms, String text, int weight) {
        for (String token : VietnameseTextUtils.tokenize(text)) {
            if (token.length() >= 2 && !STOPWORDS.contains(token)) {
                terms.merge(token, weight, Integer::sum);
            }
        }
    }

    // ================= Tiện ích =================

    static String embeddingText(ProductDoc d) {
        StringBuilder sb = new StringBuilder();
        sb.append(d.getName()).append(". ");
        if (d.getCategory() != null) {
            sb.append("Danh mục: ").append(d.getCategory()).append(". ");
        }
        if (d.getBrand() != null) {
            sb.append("Thương hiệu: ").append(d.getBrand()).append(". ");
        }
        if (d.getShortDescription() != null) {
            sb.append(d.getShortDescription()).append(' ');
        }
        if (d.getAttributes() != null && !d.getAttributes().isEmpty()) {
            sb.append("Đặc điểm: ").append(d.getAttributes()).append(". ");
        }
        if (d.getTags() != null) {
            sb.append("Nhãn: ").append(d.getTags()).append(". ");
        }
        if (d.getDescription() != null) {
            String desc = d.getDescription();
            sb.append(desc, 0, Math.min(desc.length(), 600));
        }
        return sb.toString();
    }

    /**
     * Gom giá trị thuộc tính (màu, chất liệu, kích thước, phong cách…) từ JSON properties/specifications.
     */
    private String extractAttributes(String propertiesJson, String specificationsJson) {
        Set<String> values = new LinkedHashSet<>();
        for (String json : new String[]{propertiesJson, specificationsJson}) {
            if (json == null || json.isBlank()) {
                continue;
            }
            try {
                JsonNode content = objectMapper.readTree(json).path("content");
                for (JsonNode item : content) {
                    JsonNode value = item.path("value");
                    String name = item.path("name").asText("");
                    if (value.isArray()) {
                        value.forEach(v -> values.add(name + " " + v.asText()));
                    } else if (!value.asText("").isBlank()) {
                        values.add(name + " " + value.asText());
                    }
                }
            } catch (Exception ignored) {
                // JSON thuộc tính không hợp lệ: bỏ qua
            }
        }
        return String.join("; ", values);
    }

    static PriceRange parsePriceRange(String text) {
        Double min = null;
        Double max = null;
        Matcher matcher = PRICE_PATTERN.matcher(text);
        while (matcher.find()) {
            double value;
            try {
                String raw = matcher.group(2);
                // "1.5" / "1,5" là số thập phân, "500.000" / "500 000" là phân tách hàng nghìn
                value = raw.matches("\\d+[.,]\\d{1,2}")
                        ? Double.parseDouble(raw.replace(',', '.'))
                        : Double.parseDouble(raw.replaceAll("[ .,]", ""));
            } catch (NumberFormatException e) {
                continue;
            }
            String unit = matcher.group(3);
            if (unit == null) {
                // "dưới 5" hiểu là triệu, "dưới 500" hiểu là nghìn
                value = value < 100 ? value * 1_000_000 : value < 100_000 ? value * 1_000 : value;
            } else if (unit.equals("trieu") || unit.equals("tr")) {
                value *= 1_000_000;
            } else if (unit.equals("k") || unit.equals("nghin") || unit.equals("ngan")) {
                value *= 1_000;
            }
            String op = matcher.group(1);
            if (op.equals("duoi") || op.equals("toi da") || op.equals("khong qua") || op.equals("re hon")) {
                max = value;
            } else {
                min = value;
            }
        }
        return new PriceRange(min, max);
    }

    private static byte[] toBytes(float[] vector) {
        ByteBuffer buffer = ByteBuffer.allocate(vector.length * 4).order(ByteOrder.LITTLE_ENDIAN);
        for (float v : vector) {
            buffer.putFloat(v);
        }
        return buffer.array();
    }

    private static float[] fromBytes(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN);
        float[] vector = new float[bytes.length / 4];
        for (int i = 0; i < vector.length; i++) {
            vector[i] = buffer.getFloat();
        }
        return vector;
    }

    private static String sha256(String text) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(text.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    // ================= Kiểu dữ liệu =================

    @Getter
    @Builder
    public static class ProductDoc {
        private final Long id;
        private final String name;
        private final String slug;
        private final String shortDescription;
        private final String description;
        private final String attributes;
        private final String category;
        private final String categorySlug;
        private final String brand;
        private final Double minPrice;
        private final Double maxPrice;
        private final String thumbnail;
        private final double rating;
        private final int reviewCount;
        private final String tags;
        private final Instant updatedAt;
        @lombok.Setter
        private int stock;
        @lombok.Setter
        private Map<String, Integer> terms;
        @lombok.Setter
        private int termLength;

        public String getNormName() {
            return VietnameseTextUtils.normalize(name);
        }
    }

    @Getter
    @Builder
    public static class SearchQuery {
        private final String text;
        private final List<String> categorySlugs;
        private final Double minPrice;
        private final Double maxPrice;
        private final boolean inStockOnly;
        private final String sort;
        @Builder.Default
        private final int limit = 6;
    }

    @Getter
    @RequiredArgsConstructor
    public static class SearchResult {
        private final List<ProductDoc> products;
        private final int candidateCount;
        private final boolean semanticUsed;
        private final PriceRange appliedPriceRange;
        private final Map<Long, Double> semanticScores;
    }

    @Getter
    @RequiredArgsConstructor
    public static class IndexStats {
        private final int products;
        private final int vectors;
        private final Instant loadedAt;
        private final boolean embeddingActive;
    }

    @Getter
    @RequiredArgsConstructor
    public static class PriceRange {
        private final Double min;
        private final Double max;

        static PriceRange of(Double min, Double max) {
            return new PriceRange(min, max);
        }

        boolean isEmpty() {
            return min == null && max == null;
        }

        boolean overlaps(Double productMin, Double productMax) {
            if (isEmpty()) {
                return true;
            }
            if (productMin == null) {
                return false;
            }
            double low = productMin;
            double high = productMax == null ? productMin : productMax;
            return (max == null || low <= max) && (min == null || high >= min);
        }
    }

    private static final class Snapshot {
        private final List<ProductDoc> docs;
        private final Map<Long, ProductDoc> byId;
        private final Map<Long, float[]> vectors;
        private final Map<String, Double> idf;
        private final double avgLength;
        private final Instant loadedAt;

        private Snapshot(List<ProductDoc> docs, Map<Long, float[]> vectors, Map<String, Double> idf, double avgLength, Instant loadedAt) {
            this.docs = docs;
            this.byId = docs.stream().collect(Collectors.toMap(ProductDoc::getId, d -> d, (a, b) -> a));
            this.vectors = vectors;
            this.idf = idf;
            this.avgLength = avgLength;
            this.loadedAt = loadedAt;
        }

        static Snapshot empty() {
            return new Snapshot(List.of(), Map.of(), Map.of(), 1, null);
        }

        static Snapshot build(List<ProductDoc> docs, Map<Long, float[]> vectors) {
            Map<String, Integer> df = new HashMap<>();
            long totalLength = 0;
            for (ProductDoc d : docs) {
                d.getTerms().keySet().forEach(t -> df.merge(t, 1, Integer::sum));
                totalLength += d.getTermLength();
            }
            int n = Math.max(1, docs.size());
            Map<String, Double> idf = new HashMap<>();
            df.forEach((term, count) -> idf.put(term, Math.log(1 + (n - count + 0.5) / (count + 0.5))));
            Map<Long, float[]> alive = new HashMap<>();
            docs.forEach(d -> {
                if (vectors.containsKey(d.getId())) {
                    alive.put(d.getId(), vectors.get(d.getId()));
                }
            });
            return new Snapshot(List.copyOf(docs), alive, idf, Math.max(1.0, (double) totalLength / n), Instant.now());
        }
    }

}
