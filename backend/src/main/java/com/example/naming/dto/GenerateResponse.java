package com.example.naming.dto;

import java.util.List;

public class GenerateResponse {
    private List<NameItem> names;

    public GenerateResponse(List<NameItem> names) { this.names = names; }
    public List<NameItem> getNames() { return names; }
    public void setNames(List<NameItem> names) { this.names = names; }

    public static class NameItem {
        private String text;
        private String surname;
        private String givenName;
        private List<String> sources;
        private List<String> themes;

        public NameItem(String text, String surname, String givenName, List<String> sources, List<String> themes) {
            this.text = text;
            this.surname = surname;
            this.givenName = givenName;
            this.sources = sources;
            this.themes = themes;
        }

        public String getText() { return text; }
        public void setText(String text) { this.text = text; }
        public String getSurname() { return surname; }
        public void setSurname(String surname) { this.surname = surname; }
        public String getGivenName() { return givenName; }
        public void setGivenName(String givenName) { this.givenName = givenName; }
        public List<String> getSources() { return sources; }
        public void setSources(List<String> sources) { this.sources = sources; }
        public List<String> getThemes() { return themes; }
        public void setThemes(List<String> themes) { this.themes = themes; }
    }
}
