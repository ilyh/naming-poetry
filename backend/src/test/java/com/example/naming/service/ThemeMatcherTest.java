package com.example.naming.service;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ThemeMatcherTest {

    private final ThemeMatcher matcher = new ThemeMatcher();

    @Test
    void testBestTheme_ShanShui() {
        assertEquals("山水", matcher.getBestTheme("山气日夕佳飞鸟相与还"));
    }

    @Test
    void testBestTheme_AiQing() {
        assertEquals("爱情", matcher.getBestTheme("情意思念盟誓痴心"));
    }

    @Test
    void testBestTheme_NoMatch() {
        assertNull(matcher.getBestTheme("一二三四五六七八"));
    }

    @Test
    void testMatchesAny_True() {
        assertTrue(matcher.matchesAny("山气日夕佳", List.of("山水", "田园")));
    }

    @Test
    void testMatchesAny_False() {
        assertFalse(matcher.matchesAny("山气日夕佳", List.of("爱情", "豪迈")));
    }

    @Test
    void testBestTheme_EmptyString() {
        assertNull(matcher.getBestTheme(""));
    }
}
