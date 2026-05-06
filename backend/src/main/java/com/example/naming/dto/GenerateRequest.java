package com.example.naming.dto;

import java.util.List;

public class GenerateRequest {

    private String surname;
    private String keyword;
    private List<String> themes;
    private List<String> sources;
    private Integer count = 5;
    private Integer length = 2;

    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }
    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }
    public List<String> getThemes() { return themes; }
    public void setThemes(List<String> themes) { this.themes = themes; }
    public List<String> getSources() { return sources; }
    public void setSources(List<String> sources) { this.sources = sources; }
    public Integer getCount() { return count; }
    public void setCount(Integer count) { this.count = count; }
    public Integer getLength() { return length; }
    public void setLength(Integer length) { this.length = length; }
}
