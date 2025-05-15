package com.example.ecommerceweb.util;

import java.text.Normalizer;
import java.util.regex.Pattern;

public class Static {

    public static String convertToSlug(String input) {

        // Bỏ dấu tiếng Việt
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        input = pattern.matcher(normalized).replaceAll("");

        input = input.trim().toLowerCase();
        input = input.replaceAll("[^a-z0-9\\s-]", "");
        input = input.replaceAll("[\\s-]+", "-");
        input = input.replaceAll("^-+|-+$", "");

        return input;
    }
}
