package rpgram.core;

import java.io.*;
import java.util.Properties;

public class Config {
    private final String fileName;
    private final Properties properties;

    public class ConfigException extends Exception {
        public ConfigException(String message) {
            super(message);
        }
    }

    /**
     * The main class constructor.
     *
     * @param fileName A configuration file name.
     */
    public Config(String fileName) {
        this.fileName = fileName;
        this.properties = new Properties();
    }

    /**
     * Load configuration from the specified file.
     *
     * @throws ConfigException on errors.
     */
    public void load() throws ConfigException {
        try {
            FileInputStream stream = new FileInputStream(this.fileName);
            properties.load(stream);
            stream.close();
        } catch (FileNotFoundException e) {
            throw new ConfigException("Config file not found: "
                + this.fileName);
        } catch (IOException e) {
            throw new ConfigException("Could not read config file: "
                + this.fileName);
        }
    }

    /**
     * Get property value.
     *
     * @param key A property name.
     * @return Property value as a string.
     */
    public String get(String key) {
        return this.properties.getProperty(key);
    }
}
