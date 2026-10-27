package com.example.naming.dto;

import java.util.List;

public class VoteCreateRequest {
    private String sessionId;
    private String surname;
    private List<ItemInput> items;

    public static class ItemInput {
        private String text;
        private String surname;
        private String givenName;
        private String sourceNote;
        private List<String> sources;
        private Long poemId;

        public String getText() { return text; }
        public void setText(String text) { this.text = text; }
        public String getSurname() { return surname; }
        public void setSurname(String surname) { this.surname = surname; }
        public String getGivenName() { return givenName; }
        public void setGivenName(String givenName) { this.givenName = givenName; }
        public String getSourceNote() { return sourceNote; }
        public void setSourceNote(String sourceNote) { this.sourceNote = sourceNote; }
        public List<String> getSources() { return sources; }
        public void setSources(List<String> sources) { this.sources = sources; }
        public Long getPoemId() { return poemId; }
        public void setPoemId(Long poemId) { this.poemId = poemId; }
    }

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }
    public List<ItemInput> getItems() { return items; }
    public void setItems(List<ItemInput> items) { this.items = items; }
}
