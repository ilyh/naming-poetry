package com.example.naming.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "name_vote")
public class NameVote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "creator_session_id", nullable = false, length = 64)
    private String creatorSessionId;

    @Column(nullable = false, length = 4)
    private String surname;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public NameVote() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCreatorSessionId() { return creatorSessionId; }
    public void setCreatorSessionId(String creatorSessionId) { this.creatorSessionId = creatorSessionId; }
    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
