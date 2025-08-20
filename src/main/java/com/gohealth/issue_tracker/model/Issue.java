package com.gohealth.issue_tracker.model;

import java.time.LocalDateTime;

public class Issue {
    private String id;
    private String description;
    private String parentId;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // --- Constructors ---
    public Issue(String id, String description, String parentId, String status) {
        this.id = id;
        this.description = description;
        this.parentId = parentId;
        this.status = status;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Issue(){}

    // --- Getters and Setters ---
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getParentId() { return parentId; }
    public void setParentId(String parentId) { this.parentId = parentId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }


    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return id + " | " + description + " | status=" + status;
    }
}
