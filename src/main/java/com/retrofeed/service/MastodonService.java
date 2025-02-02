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

public class MastodonService {
    private final CloseableHttpClient httpClient;
    private final ObjectMapper mapper;
    private String instanceUrl;
    private String accessToken;
    
    public MastodonService() {
        this.httpClient = HttpClients.createDefault();
        this.mapper = new ObjectMapper();
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
        if (!isConfigured()) {
            throw new IOException("MastodonService not properly configured");
        }

        HttpGet request = new HttpGet(instanceUrl + "/api/v1/timelines/home");
        request.addHeader("Authorization", "Bearer " + accessToken);
        
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            if (response.getCode() != 200) {
                throw new IOException("API returned status: " + response.getCode());
            }
            
            List<MastodonPost> posts = mapper.readValue(
                response.getEntity().getContent(),
                new TypeReference<List<MastodonPost>>() {}
            );
            
            List<String> formattedPosts = new ArrayList<>();
            for (MastodonPost post : posts) {
                formattedPosts.add(post.toTextDisplay());
            }
            
            return formattedPosts;
        }
    }
}
