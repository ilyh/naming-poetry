package com.example.naming.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "name_vote_record", uniqueConstraints = @UniqueConstraint(columnNames = {"vote_id", "session_id"}))
public class NameVoteRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "vote_id", nullable = false)
    private Long voteId;

    @Column(name = "session_id", nullable = false, length = 64)
    private String sessionId;

    @Column(name = "item_id", nullable = false)
    private Long itemId;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    public NameVoteRecord() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getVoteId() { return voteId; }
    public void setVoteId(Long voteId) { this.voteId = voteId; }
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    public Long getItemId() { return itemId; }
    public void setItemId(Long itemId) { this.itemId = itemId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
