package com.example.ecommerceweb.util;

public class Static {
    public static int PAGINATION_LIMIT = 8;
    public static int LATEST_LIMIT = 5;
    public static int TOP_RATING_LIMIT = 5;
    public static int PAGE_SLIDE = 3;

    public static String convertToSlug(String input) {
        input = input.trim().toLowerCase();
        input = input.replaceAll("[^a-z0-9\\s-]", "");
        input = input.replaceAll("[\\s-]+", "-");
        input = input.replaceAll("^-+|-+$", "");
        return input;
    }
}
