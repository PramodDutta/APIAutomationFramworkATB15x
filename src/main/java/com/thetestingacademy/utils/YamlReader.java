package com.thetestingacademy.utils;

import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Map;

public class YamlReader {
    private final Map<String, Object> yamlData;

    public YamlReader(String fileName) {
        Yaml yaml = new Yaml();
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName);
        if (inputStream == null) {
            throw new RuntimeException("YAML file not found: " + fileName);
        }
        this.yamlData = yaml.load(inputStream);
    }

    public String getString(String path) {
        Object value = getValue(path);
        return value != null ? String.valueOf(value) : null;
    }

    public boolean getBoolean(String path) {
        Object value = getValue(path);
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        if (value instanceof String) {
            return Boolean.parseBoolean((String) value);
        }
        throw new RuntimeException("Value at path '" + path + "' is not a boolean");
    }

    public int getInt(String path) {
        Object value = getValue(path);
        if (value instanceof Integer) {
            return (Integer) value;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        if (value instanceof String) {
            return Integer.parseInt((String) value);
        }
        throw new RuntimeException("Value at path '" + path + "' is not an integer");
    }

    private Object getValue(String path) {
        String[] keys = path.split("\\.");
        Object current = yamlData;
        for (String key : keys) {
            if (!(current instanceof Map)) {
                return null;
            }
            current = ((Map<?, ?>) current).get(key);
            if (current == null) {
                return null;
            }
        }
        return current;
    }
}