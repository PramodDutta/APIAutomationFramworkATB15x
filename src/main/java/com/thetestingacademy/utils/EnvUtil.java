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

    public static String getGroqApiKey() {
        return getRequired("GROQ_API_KEY");
    }

    public static String getRequired(String key) {
        String value = resolve(key);
        if (value == null) {
            throw new IllegalStateException("Missing required env variable: " + key);
        }
        return value;
    }

    public static String getOptional(String key, String defaultValue) {
        String value = resolve(key);
        return value == null ? defaultValue : value;
    }

    private static String resolve(String key) {
        String systemPropertyValue = System.getProperty(key);
        if (systemPropertyValue != null) {
            return systemPropertyValue;
        }

        String envValue = System.getenv(key);
        if (envValue != null) {
            return envValue;
        }

        String dotenvValue = dotenv.get(key);
        if (dotenvValue != null) {
            return dotenvValue;
        }

        return null;
    }
}
