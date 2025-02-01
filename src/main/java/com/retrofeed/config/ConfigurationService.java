package com.retrofeed.config;

import javax.servlet.ServletContext;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigurationService {
    private static final String CONFIG_FILE = "/etc/retrofeed/config.properties";
    private final Properties properties;
    
    public ConfigurationService() throws IOException {
        properties = new Properties();
        try (FileInputStream fis = new FileInputStream(CONFIG_FILE)) {
            properties.load(fis);
        }
    }
    
    public String getMastodonInstance() {
        return properties.getProperty("mastodon.instance", "https://mastodon.social");
    }
    
    public String getMastodonToken() {
        return properties.getProperty("mastodon.token");
    }
    
    public boolean isConfigured() {
        return getMastodonToken() != null && !getMastodonToken().isEmpty();
    }
}
