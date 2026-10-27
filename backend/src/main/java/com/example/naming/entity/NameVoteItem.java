package com.example.naming.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "name_vote_item")
public class NameVoteItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "vote_id", nullable = false)
    private Long voteId;

    @Column(name = "full_name", nullable = false, length = 10)
    private String fullName;

    @Column(name = "given_name", nullable = false, length = 4)
    private String givenName;

    @Column(name = "source_note", length = 255)
    private String sourceNote;

    @Column(name = "source_sentence", length = 255)
    private String sourceSentence;

    @Column(name = "poem_id")
    private Long poemId;

    @Column(nullable = false)
    private Integer votes = 0;

    public NameVoteItem() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getVoteId() { return voteId; }
    public void setVoteId(Long voteId) { this.voteId = voteId; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getGivenName() { return givenName; }
    public void setGivenName(String givenName) { this.givenName = givenName; }
    public String getSourceNote() { return sourceNote; }
    public void setSourceNote(String sourceNote) { this.sourceNote = sourceNote; }
    public String getSourceSentence() { return sourceSentence; }
    public void setSourceSentence(String sourceSentence) { this.sourceSentence = sourceSentence; }
    public Long getPoemId() { return poemId; }
    public void setPoemId(Long poemId) { this.poemId = poemId; }
    public Integer getVotes() { return votes; }
    public void setVotes(Integer votes) { this.votes = votes; }
}
