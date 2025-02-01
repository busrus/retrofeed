package com.retrofeed.config;

import javax.servlet.ServletContextListener;
import javax.servlet.ServletContextEvent;
import javax.servlet.annotation.WebListener;

@WebListener
public class RetroFeedContextListener implements ServletContextListener {
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            ConfigurationService config = new ConfigurationService();
            sce.getServletContext().setAttribute("config", config);
            
            if (!config.isConfigured()) {
                sce.getServletContext().log("WARNING: Mastodon token not configured!");
            }
        } catch (Exception e) {
            sce.getServletContext().log("ERROR: Failed to initialize configuration", e);
        }
    }
    
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Cleanup if needed
    }
}
