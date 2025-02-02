package com.retrofeed.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MastodonPost {
    private String id;
    private String content;
    
    @JsonProperty("account")
    private Account account;
    
    @JsonProperty("created_at")
    private String createdAt;

    @JsonProperty("reblog")
    private MastodonPost reblog;
    
    @JsonProperty("media_attachments")
    private List<MediaAttachment> mediaAttachments;
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MediaAttachment {
        private String type;
        private String description;  // This is the alt text
        
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }
    
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
    
    // All getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public Account getAccount() { return account; }
    public void setAccount(Account account) { this.account = account; }
    
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    
    public MastodonPost getReblog() { return reblog; }
    public void setReblog(MastodonPost reblog) { this.reblog = reblog; }
    
    public List<MediaAttachment> getMediaAttachments() { return mediaAttachments; }
    public void setMediaAttachments(List<MediaAttachment> mediaAttachments) { 
        this.mediaAttachments = mediaAttachments; 
    }
    
    public String toTextDisplay() {
        StringBuilder sb = new StringBuilder();
        
        // Handle boosts (reblogs)
        if (reblog != null) {
            sb.append("From: ").append(account.getDisplayName())
              .append(" (@").append(account.getUsername()).append(")")
              .append("\n");
            sb.append("Boosted:\n");
            sb.append("  From: ").append(reblog.getAccount().getDisplayName())
              .append(" (@").append(reblog.getAccount().getUsername()).append(")")
              .append("\n");
            sb.append("  Date: ").append(reblog.getCreatedAt()).append("\n\n");
            
            // Use the reblogged post's content
            String boostContent = reblog.getContent().replaceAll("<[^>]*>", "")
                                   .replaceAll("&quot;", "\"")
                                   .replaceAll("&amp;", "&")
                                   .replaceAll("&lt;", "<")
                                   .replaceAll("&gt;", ">");
            sb.append("  ").append(boostContent.replace("\n", "\n  "));
            
            // Add media descriptions from reblogged post
            if (reblog.getMediaAttachments() != null && !reblog.getMediaAttachments().isEmpty()) {
                sb.append("\n\n  [Images]:\n");
                for (int i = 0; i < reblog.getMediaAttachments().size(); i++) {
                    MediaAttachment media = reblog.getMediaAttachments().get(i);
                    if (media.getDescription() != null && !media.getDescription().isEmpty()) {
                        sb.append("  ").append(i + 1).append(": ")
                          .append(media.getDescription()).append("\n");
                    } else {
                        sb.append("  ").append(i + 1).append(": [").append(media.getType())
                          .append(" without description]\n");
                    }
                }
            }
        } else {
            // Regular post
            sb.append("From: ").append(account.getDisplayName())
              .append(" (@").append(account.getUsername()).append(")")
              .append("\n");
            sb.append("Date: ").append(createdAt).append("\n\n");
            
            // Strip HTML from content
            String plainContent = content.replaceAll("<[^>]*>", "")
                                   .replaceAll("&quot;", "\"")
                                   .replaceAll("&amp;", "&")
                                   .replaceAll("&lt;", "<")
                                   .replaceAll("&gt;", ">");
            sb.append(plainContent);
            
            // Add media descriptions
            if (mediaAttachments != null && !mediaAttachments.isEmpty()) {
                sb.append("\n\n[Images]:\n");
                for (int i = 0; i < mediaAttachments.size(); i++) {
                    MediaAttachment media = mediaAttachments.get(i);
                    if (media.getDescription() != null && !media.getDescription().isEmpty()) {
                        sb.append(i + 1).append(": ").append(media.getDescription()).append("\n");
                    } else {
                        sb.append(i + 1).append(": [").append(media.getType())
                          .append(" without description]\n");
                    }
                }
            }
        }
        
        return sb.toString();
    }
}
