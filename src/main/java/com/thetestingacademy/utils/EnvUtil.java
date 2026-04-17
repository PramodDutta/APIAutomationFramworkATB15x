package com.thetestingacademy.utils;

import io.github.cdimascio.dotenv.Dotenv;

public class EnvUtil {
    private static final Dotenv dotenv = Dotenv.configure()
            .ignoreIfMissing()
            .load();

    public static String getUsername() {
        return getRequired("USERNAME");
    }

    public static String getPassword() {
        return getRequired("PASSWORD");
    }

    private static String getRequired(String key) {
        String value = dotenv.get(key);
        if (value == null) {
            throw new IllegalStateException("Missing required env variable: " + key);
        }
        return value;
    }
}
