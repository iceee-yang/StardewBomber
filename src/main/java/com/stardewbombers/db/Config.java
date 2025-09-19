package com.stardewbombers.db;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

final class Config {
    private static final Properties props = new Properties();

    static {
        try (InputStream in = Config.class.getClassLoader().getResourceAsStream("app.properties")) {
            if (in != null) {
                props.load(in);
            }
        } catch (IOException ignored) {}
    }

    static String get(String key, String def) {
        String v = System.getenv(key);
        if (v != null && !v.isEmpty()) return v;
        v = System.getProperty(key);
        if (v != null && !v.isEmpty()) return v;
        v = props.getProperty(key);
        return (v == null || v.isEmpty()) ? def : v;
    }
}
