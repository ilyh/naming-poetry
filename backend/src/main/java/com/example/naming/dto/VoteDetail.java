package com.example.naming.dto;

import java.util.List;

public class VoteDetail {
    private Long id;
    private String surname;
    private String createdAt;
    private int totalVotes;
    private Long myVoteItemId;
    private List<Item> items;

    public static class Item {
        private Long id;
        private String fullName;
        private String surname;
        private String givenName;
        private String sourceNote;
        private String sourceSentence;
        private Long poemId;
        private int votes;

        public Item(Long id, String fullName, String givenName, String sourceNote,
                    String sourceSentence, Long poemId, int votes) {
            this.id = id;
            this.fullName = fullName;
            // fullName = surname + givenName，按后缀反推候选项自己的姓氏
            this.surname = fullName != null && givenName != null
                    && fullName.length() >= givenName.length()
                    ? fullName.substring(0, fullName.length() - givenName.length())
                    : "";
            this.givenName = givenName;
            this.sourceNote = sourceNote;
            this.sourceSentence = sourceSentence;
            this.poemId = poemId;
            this.votes = votes;
        }

        public Long getId() { return id; }
        public String getFullName() { return fullName; }
        public String getSurname() { return surname; }
        public String getGivenName() { return givenName; }
        public String getSourceNote() { return sourceNote; }
        public String getSourceSentence() { return sourceSentence; }
        public Long getPoemId() { return poemId; }
        public int getVotes() { return votes; }
    }

    public VoteDetail(Long id, String surname, String createdAt, Long myVoteItemId, List<Item> items) {
        this.id = id;
        this.surname = surname;
        this.createdAt = createdAt;
        this.myVoteItemId = myVoteItemId;
        this.items = items;
        this.totalVotes = items.stream().mapToInt(Item::getVotes).sum();
    }

    public Long getId() { return id; }
    public String getSurname() { return surname; }
    public String getCreatedAt() { return createdAt; }
    public int getTotalVotes() { return totalVotes; }
    public Long getMyVoteItemId() { return myVoteItemId; }
    public List<Item> getItems() { return items; }
}
