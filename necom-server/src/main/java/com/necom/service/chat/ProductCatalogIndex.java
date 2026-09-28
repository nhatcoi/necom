package com.necom.service.chat;

import com.necom.entity.general.Image;
import com.necom.entity.product.Product;
import com.necom.entity.product.Variant;
import com.necom.repository.product.ProductRepository;
import com.necom.utils.VietnameseTextUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Chỉ mục sản phẩm trong bộ nhớ cho chatbot: tìm theo từ khóa đã bỏ dấu, có lọc khoảng giá
 * ("dưới 5 triệu", "trên 500k"). Catalog nhỏ nên tải toàn bộ và làm mới định kỳ; khi catalog lớn
 * có thể thay bằng vector store (Qdrant) mà không đổi giao diện search().
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ProductCatalogIndex {

    private static final long TTL_MILLIS = 10 * 60 * 1000L;

    private static final Set<String> STOPWORDS = Set.of(
            "toi", "minh", "ban", "em", "anh", "chi", "shop", "oi", "a", "ah", "nhe", "nha", "voi", "va", "hoac",
            "muon", "can", "co", "khong", "ko", "k", "cho", "la", "cua", "nao", "gi", "the", "nay", "do", "mot",
            "nhung", "cac", "duoc", "hoi", "xem", "tim", "mua", "gia", "bao", "nhieu", "vay", "sao", "lam", "nhu",
            "thi", "se", "da", "dang", "rat", "qua", "hay", "hon", "den", "trong", "tren", "duoi", "ve",
            "nen", "giup", "van", "san", "pham", "loai", "mau", "kieu", "dep", "re", "tot", "hang", "con",
            "khoang", "tam", "trieu", "tr", "nghin", "ngan", "dong", "vnd", "d", "goi", "y", "biet", "them",
            "thich", "phu", "hop", "de", "o", "nhat", "moi", "ai", "cai", "chiec", "bo", "ne", "di", "roi"
    );

    // Ví dụ: "dưới 5 triệu", "tối đa 500k", "trên 2tr". Không dùng "từ" vì bỏ dấu trùng với "tủ"
    private static final Pattern PRICE_PATTERN = Pattern.compile(
            "\\b(duoi|toi da|khong qua|re hon|tren|hon)\\s*(\\d+(?:[ .,]\\d+)?)\\s*(trieu|tr|k|nghin|ngan|d|dong)?\\b");

    private final ProductRepository productRepository;
    private final TransactionTemplate transactionTemplate;

    private volatile List<ProductDoc> docs = List.of();
    private volatile List<String> categories = List.of();
    private volatile long loadedAt = 0;

    public List<ProductDoc> search(String query, int limit) {
        refreshIfStale();
        String normalized = VietnameseTextUtils.normalize(query);
        PriceRange range = parsePriceRange(VietnameseTextUtils.removeAccents(query));

        List<String> tokens = VietnameseTextUtils.tokenize(normalized).stream()
                .filter(t -> t.length() >= 2 && !STOPWORDS.contains(t) && !t.chars().allMatch(Character::isDigit))
                .distinct()
                .collect(Collectors.toList());
        if (tokens.isEmpty()) {
            return List.of();
        }

        List<Scored> scored = new ArrayList<>();
        for (ProductDoc doc : docs) {
            if (!range.accepts(doc.getMinPrice())) {
                continue;
            }
            int score = score(doc, tokens);
            if (score >= 2) {
                scored.add(new Scored(doc, score));
            }
        }
        return scored.stream()
                .sorted(Comparator.comparingInt(Scored::getScore).reversed()
                        .thenComparing(s -> s.getDoc().getMinPrice(), Comparator.nullsLast(Comparator.naturalOrder())))
                .limit(limit)
                .map(Scored::getDoc)
                .collect(Collectors.toList());
    }

    public List<ProductDoc> findByIds(List<Long> ids) {
        refreshIfStale();
        return ids.stream()
                .map(id -> docs.stream().filter(d -> d.getId().equals(id)).findFirst().orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public List<String> categories() {
        refreshIfStale();
        return categories;
    }

    private int score(ProductDoc doc, List<String> tokens) {
        int score = 0;
        for (String token : tokens) {
            if (VietnameseTextUtils.containsWord(doc.getNormName(), token)) {
                score += 3;
            } else if (VietnameseTextUtils.containsWord(doc.getNormCategory(), token)
                    || VietnameseTextUtils.containsWord(doc.getNormBrand(), token)) {
                score += 2;
            } else if (VietnameseTextUtils.containsWord(doc.getNormDescription(), token)) {
                score += 1;
            }
        }
        // Thưởng khi khớp cụm 2 từ liên tiếp trong tên ("ghe sofa", "ban an")
        for (int i = 0; i + 1 < tokens.size(); i++) {
            if (VietnameseTextUtils.containsWord(doc.getNormName(), tokens.get(i) + " " + tokens.get(i + 1))) {
                score += 3;
            }
        }
        return score;
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

    private void refreshIfStale() {
        if (System.currentTimeMillis() - loadedAt < TTL_MILLIS) {
            return;
        }
        synchronized (this) {
            if (System.currentTimeMillis() - loadedAt < TTL_MILLIS) {
                return;
            }
            try {
                List<ProductDoc> loaded = transactionTemplate.execute(status -> productRepository.findAll().stream()
                        .filter(p -> Integer.valueOf(1).equals(p.getStatus()))
                        .map(ProductCatalogIndex::toDoc)
                        .collect(Collectors.toList()));
                docs = loaded == null ? List.of() : loaded;
                categories = docs.stream()
                        .map(ProductDoc::getCategory)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toCollection(LinkedHashSet::new))
                        .stream().collect(Collectors.toList());
                loadedAt = System.currentTimeMillis();
                log.info("Chatbot product index loaded {} products", docs.size());
            } catch (Exception e) {
                log.warn("Cannot load chatbot product index: {}", e.getMessage());
            }
        }
    }

    private static ProductDoc toDoc(Product product) {
        List<Double> prices = product.getVariants().stream()
                .map(Variant::getPrice)
                .filter(Objects::nonNull)
                .sorted()
                .collect(Collectors.toList());
        String thumbnail = product.getImages().stream()
                .filter(image -> Boolean.TRUE.equals(image.getIsThumbnail()))
                .map(Image::getPath)
                .findFirst()
                .orElse(product.getImages().isEmpty() ? null : product.getImages().get(0).getPath());
        String category = product.getCategory() == null ? null : product.getCategory().getName();
        String brand = product.getBrand() == null ? null : product.getBrand().getName();

        return new ProductDoc(
                product.getId(),
                product.getName(),
                product.getSlug(),
                category,
                brand,
                product.getShortDescription(),
                prices.isEmpty() ? null : prices.get(0),
                prices.isEmpty() ? null : prices.get(prices.size() - 1),
                thumbnail,
                VietnameseTextUtils.normalize(product.getName()),
                VietnameseTextUtils.normalize(category),
                VietnameseTextUtils.normalize(brand),
                VietnameseTextUtils.normalize(product.getShortDescription())
        );
    }

    @Getter
    @AllArgsConstructor
    public static class ProductDoc {
        private final Long id;
        private final String name;
        private final String slug;
        private final String category;
        private final String brand;
        private final String shortDescription;
        private final Double minPrice;
        private final Double maxPrice;
        private final String thumbnail;
        private final String normName;
        private final String normCategory;
        private final String normBrand;
        private final String normDescription;
    }

    @Getter
    @AllArgsConstructor
    static class PriceRange {
        private final Double min;
        private final Double max;

        boolean accepts(Double price) {
            if (price == null) {
                return min == null && max == null;
            }
            return (min == null || price >= min) && (max == null || price <= max);
        }
    }

    @Getter
    @AllArgsConstructor
    private static class Scored {
        private final ProductDoc doc;
        private final int score;
    }

}
