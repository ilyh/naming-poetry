package com.example.naming.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "poem_word", indexes = {
    @Index(name = "idx_word", columnList = "word"),
    @Index(name = "idx_meaning_tag", columnList = "meaningTag"),
    @Index(name = "idx_poem_id", columnList = "poem_id")
})
public class PoemWord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "poem_id", nullable = false)
    private Poem poem;

    @Column(nullable = false, length = 1)
    private String word;

    @Column(nullable = false)
    private Integer position;

    @Column(length = 50)
    private String context;

    @Column(length = 1)
    private String prevWord;

    @Column(length = 1)
    private String nextWord;

    @Column(length = 50)
    private String meaningTag;

    public PoemWord() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Poem getPoem() { return poem; }
    public void setPoem(Poem poem) { this.poem = poem; }
    public String getWord() { return word; }
    public void setWord(String word) { this.word = word; }
    public Integer getPosition() { return position; }
    public void setPosition(Integer position) { this.position = position; }
    public String getContext() { return context; }
    public void setContext(String context) { this.context = context; }
    public String getPrevWord() { return prevWord; }
    public void setPrevWord(String prevWord) { this.prevWord = prevWord; }
    public String getNextWord() { return nextWord; }
    public void setNextWord(String nextWord) { this.nextWord = nextWord; }
    public String getMeaningTag() { return meaningTag; }
    public void setMeaningTag(String meaningTag) { this.meaningTag = meaningTag; }
}
