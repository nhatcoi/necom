package com.necom.utils;

import java.security.SecureRandom;

public class RandomStringUtils {

    private static final String ALPHANUMERIC = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    private RandomStringUtils() {
    }

    // Thay cho net.bytebuddy.utility.RandomString (không còn là dependency bắc cầu từ Hibernate 6)
    public static String alphanumeric(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(ALPHANUMERIC.charAt(RANDOM.nextInt(ALPHANUMERIC.length())));
        }
        return sb.toString();
    }

}
