package com.necom.utils;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class VietnameseTextUtils {

    private static final Pattern DIACRITICS = Pattern.compile("\\p{M}+");
    private static final Pattern NON_WORD = Pattern.compile("[^a-z0-9]+");

    private VietnameseTextUtils() {
    }

    /**
     * Bỏ dấu, chuyển thường, gộp khoảng trắng: "Ghế Sofa  Đơn" -> "ghe sofa don".
     */
    public static String normalize(String text) {
        return NON_WORD.matcher(removeAccents(text)).replaceAll(" ").trim();
    }

    /**
     * Chỉ bỏ dấu và chuyển thường, giữ nguyên dấu câu (dùng khi cần đọc số như "1.5 triệu").
     */
    public static String removeAccents(String text) {
        if (text == null) {
            return "";
        }
        return DIACRITICS.matcher(Normalizer.normalize(text, Normalizer.Form.NFD)).replaceAll("")
                .replace('đ', 'd')
                .replace('Đ', 'D')
                .toLowerCase(Locale.ROOT);
    }

    public static List<String> tokenize(String text) {
        String normalized = normalize(text);
        if (normalized.isEmpty()) {
            return List.of();
        }
        return Arrays.stream(normalized.split(" ")).collect(Collectors.toList());
    }

    /**
     * So khớp theo ranh giới từ trên chuỗi đã normalize.
     */
    public static boolean containsWord(String normalizedHaystack, String normalizedNeedle) {
        return (" " + normalizedHaystack + " ").contains(" " + normalizedNeedle + " ");
    }

}
