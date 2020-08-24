package rpgram.core;

import java.io.*;
import java.util.Properties;

public class Config {
    public static final String DEFAULT_PROXY_HOST = "127.0.0.1";
    public static final Integer DEFAULT_PROXY_PORT = 9050;

    public static final Integer DEFAULT_MAP_SIZE = 101;

    private final Properties properties = new Properties();

    public static class ConfigException extends Exception {
        public ConfigException(String message) {
            super(message);
        }
    }

    /**
     * Load configuration from the specified file.
     *
     * @param fileName A configuration file name.
     * @throws ConfigException on errors.
     */
    public void load(String fileName) throws ConfigException {
        try (var stream = new FileInputStream(fileName)) {
            properties.load(stream);
        } catch (FileNotFoundException e) {
            throw new ConfigException(
                "Config file not found: " + fileName
            );
        } catch (IOException e) {
            throw new ConfigException(
                "Could not read config file: " + fileName
            );
        }
    }

    public boolean isProxyEnabled() {
        var value = get("proxy.enabled");
        return value == null || Boolean.parseBoolean(value);
    }

    public String getProxyHost() {
        String value = get("proxy.host");
        return value != null
            ? value
            : DEFAULT_PROXY_HOST;
    }

    public int getProxyPort() {
        String value = get("proxy.port");
        return value != null
            ? Integer.parseInt(value)
            : DEFAULT_PROXY_PORT;
    }

    public int getMapSize() {
        String value = get("game.map.size");
        return value != null
            ? Integer.parseInt(value)
            : DEFAULT_MAP_SIZE;
    }

    /**
     * Get property value.
     *
     * @param key A property name.
     * @return Property value as a string.
     */
    public String get(String key) {
        return properties.getProperty(key);
    }
}
