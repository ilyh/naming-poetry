package com.example.naming.dto;

public record PoemResponse(
    Long id,
    String title,
    String author,
    String dynasty,
    String content,
    String source
) {}
