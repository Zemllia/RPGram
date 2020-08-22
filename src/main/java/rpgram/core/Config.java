package rpgram.core;

import java.io.*;
import java.util.Properties;

public class Config {
    public static final String DEFAULT_PROXY_HOST = "127.0.0.1";
    public static final Integer DEFAULT_PROXY_PORT = 9050;

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
        var isProxyOn = get("proxy.enabled");
        return isProxyOn == null || Boolean.parseBoolean(isProxyOn);
    }

    public String getProxyHost() {
        String proxyHost = get("proxy.host");
        return proxyHost != null
            ? proxyHost
            : DEFAULT_PROXY_HOST;
    }

    public int getProxyPort() {
        String proxyPort = get("proxy.port");
        return proxyPort != null
            ? Integer.parseInt(proxyPort)
            : DEFAULT_PROXY_PORT;
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
