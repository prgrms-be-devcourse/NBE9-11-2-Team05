package com.team05.petmeeting.global.config;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Slf4j
public final class DotenvLoader {
    private static final Path DOTENV_PATH = Path.of(".env");

    private DotenvLoader() {
    }

    public static void load() {
        if (!Files.exists(DOTENV_PATH)) {
            return;
        }

        try {
            List<String> lines = Files.readAllLines(DOTENV_PATH);

            for (String rawLine : lines) {
                String line = rawLine.trim();

                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                int separatorIndex = line.indexOf('=');
                if (separatorIndex < 1) {
                    continue;
                }

                String key = line.substring(0, separatorIndex).trim();
                String value = line.substring(separatorIndex + 1).trim();

                if (key.isEmpty()) {
                    continue;
                }

                if ((value.startsWith("\"") && value.endsWith("\""))
                        || (value.startsWith("'") && value.endsWith("'"))) {
                    value = value.substring(1, value.length() - 1);
                }

                if (System.getenv(key) == null && System.getProperty(key) == null) {
                    System.setProperty(key, value);
                }
            }
        } catch (IOException e) {
            log.warn("Failed to load .env file from {}", DOTENV_PATH.toAbsolutePath(), e);
        }
    }
}
