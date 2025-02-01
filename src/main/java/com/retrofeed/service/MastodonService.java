package com.retrofeed.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.retrofeed.model.MastodonPost;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.time.Instant;

public class MastodonService {
    private final CloseableHttpClient httpClient;
    private final ObjectMapper mapper;
    private final ConcurrentHashMap<String, CacheEntry> cache;
    private String instanceUrl;
    private String accessToken;
    
    private static final long CACHE_DURATION = 60 * 1000; // 60 seconds in milliseconds
    
    private class CacheEntry {
        final List<String> data;
        final long timestamp;
        
        CacheEntry(List<String> data) {
            this.data = data;
            this.timestamp = Instant.now().toEpochMilli();
        }
        
        boolean isExpired() {
            long now = Instant.now().toEpochMilli();
            return now - timestamp > CACHE_DURATION;
        }
    }
    
    public MastodonService() {
        this.httpClient = HttpClients.createDefault();
        this.mapper = new ObjectMapper();
        this.cache = new ConcurrentHashMap<>();
    }
    
    public void configure(String instanceUrl, String accessToken) {
        this.instanceUrl = instanceUrl;
        this.accessToken = accessToken;
    }
    
    public boolean isConfigured() {
        return instanceUrl != null && !instanceUrl.isEmpty() 
            && accessToken != null && !accessToken.isEmpty();
    }
    
    public List<String> getFeed() throws IOException {
        // Check cache
        CacheEntry cachedFeed = cache.get("current_feed");
        if (cachedFeed != null && !cachedFeed.isExpired()) {
            return cachedFeed.data;
        }
        
        // Fetch from API
        HttpGet request = new HttpGet(instanceUrl + "/api/v1/timelines/home");
        request.addHeader("Authorization", "Bearer " + accessToken);
        
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            if (response.getCode() != 200) {
                throw new IOException("API returned status: " + response.getCode());
            }
            
            // Parse JSON response into list of MastodonPost objects
            List<MastodonPost> posts = mapper.readValue(
                response.getEntity().getContent(),
                new TypeReference<List<MastodonPost>>() {}
            );
            
            // Convert posts to text format
            List<String> formattedPosts = new ArrayList<>();
            for (MastodonPost post : posts) {
                formattedPosts.add(post.toTextDisplay());
            }
            
            // Cache the formatted posts
            cache.put("current_feed", new CacheEntry(formattedPosts));
            
            return formattedPosts;
        }
    }
    
    public void clearCache() {
        cache.clear();
    }
}
