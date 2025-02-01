package com.retrofeed.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MastodonPost {
    private String id;
    private String content;
    
    @JsonProperty("account")
    private Account account;
    
    @JsonProperty("created_at")
    private String createdAt;
    
    // Nested Account class
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Account {
        private String username;
        @JsonProperty("display_name")
        private String displayName;
        
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getDisplayName() { return displayName; }
        public void setDisplayName(String displayName) { this.displayName = displayName; }
    }
    
    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Account getAccount() { return account; }
    public void setAccount(Account account) { this.account = account; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    
    public String toTextDisplay() {
        StringBuilder sb = new StringBuilder();
        sb.append("From: ").append(account.getDisplayName())
          .append(" (@").append(account.getUsername()).append(")\n");
        sb.append("Date: ").append(createdAt).append("\n\n");
        // Strip HTML from content
        String plainContent = content.replaceAll("<[^>]*>", "")
                                   .replaceAll("&quot;", "\"")
                                   .replaceAll("&amp;", "&")
                                   .replaceAll("&lt;", "<")
                                   .replaceAll("&gt;", ">");
        sb.append(plainContent);
        return sb.toString();
    }
}
