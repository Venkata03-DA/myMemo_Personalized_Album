package com.venkata.mymemo.entity;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class AlbumTest {

    @Test
    void testGettersAndSetters() {
        Album album = new Album();
        album.setId(1L);
        album.setTitle("Vacation 2026");
        album.setCoverImageUrl("https://example.com/cover.jpg");
        album.setEventDate(LocalDate.of(2026, 4, 3));
        Instant now = Instant.now();
        album.setCreatedAt(now);

        assertEquals(1L, album.getId());
        assertEquals("Vacation 2026", album.getTitle());
        assertEquals("https://example.com/cover.jpg", album.getCoverImageUrl());
        assertEquals(LocalDate.of(2026, 4, 3), album.getEventDate());
        assertEquals(now, album.getCreatedAt());
    }

    @Test
    void testOnCreate_setsCreatedAtWhenNull() {
        Album album = new Album();
        assertNull(album.getCreatedAt());

        album.setTitle("Test Album");
        // Simulate @PrePersist by calling onCreate via reflection
        try {
            var method = Album.class.getDeclaredMethod("onCreate");
            method.setAccessible(true);
            method.invoke(album);
        } catch (Exception e) {
            fail("Failed to invoke onCreate: " + e.getMessage());
        }

        assertNotNull(album.getCreatedAt());
        assertEquals("Test Album", album.getTitle());
    }

    @Test
    void testOnCreate_setsDefaultTitleWhenNull() {
        Album album = new Album();
        assertNull(album.getTitle());

        try {
            var method = Album.class.getDeclaredMethod("onCreate");
            method.setAccessible(true);
            method.invoke(album);
        } catch (Exception e) {
            fail("Failed to invoke onCreate: " + e.getMessage());
        }

        assertEquals("Untitled Album", album.getTitle());
        assertNotNull(album.getCreatedAt());
    }

    @Test
    void testOnCreate_doesNotOverwriteExistingCreatedAt() {
        Album album = new Album();
        Instant existing = Instant.parse("2025-01-01T00:00:00Z");
        album.setCreatedAt(existing);

        try {
            var method = Album.class.getDeclaredMethod("onCreate");
            method.setAccessible(true);
            method.invoke(album);
        } catch (Exception e) {
            fail("Failed to invoke onCreate: " + e.getMessage());
        }

        assertEquals(existing, album.getCreatedAt());
    }
}
