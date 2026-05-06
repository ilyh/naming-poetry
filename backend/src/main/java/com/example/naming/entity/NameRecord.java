package com.example.naming.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "name_record")
public class NameRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 1)
    private String surname;

    @Column(name = "given_name", nullable = false, length = 4)
    private String givenName;

    @Column(name = "full_name", nullable = false, length = 5)
    private String fullName;

    @Column(name = "source1_id")
    private Long source1Id;

    @Column(name = "source2_id")
    private Long source2Id;

    @Column(name = "source3_id")
    private Long source3Id;

    @Column(length = 20)
    private String mode;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public NameRecord() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }
    public String getGivenName() { return givenName; }
    public void setGivenName(String givenName) { this.givenName = givenName; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public Long getSource1Id() { return source1Id; }
    public void setSource1Id(Long source1Id) { this.source1Id = source1Id; }
    public Long getSource2Id() { return source2Id; }
    public void setSource2Id(Long source2Id) { this.source2Id = source2Id; }
    public Long getSource3Id() { return source3Id; }
    public void setSource3Id(Long source3Id) { this.source3Id = source3Id; }
    public String getMode() { return mode; }
    public void setMode(String mode) { this.mode = mode; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
