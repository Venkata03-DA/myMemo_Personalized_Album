package com.venkata.mymemo.entity;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class MemoryTest {

    @Test
    void testGettersAndSetters() {
        Album album = new Album();
        album.setId(1L);
        album.setTitle("Summer");

        Memory memory = new Memory();
        memory.setId(10L);
        memory.setAlbum(album);
        memory.setImageUrl("https://example.com/photo.jpg");
        memory.setDescription("A sunny day at the beach");
        memory.setMemoryDate(LocalDate.of(2026, 4, 3));
        Instant now = Instant.now();
        memory.setCreatedAt(now);

        assertEquals(10L, memory.getId());
        assertEquals(album, memory.getAlbum());
        assertEquals("https://example.com/photo.jpg", memory.getImageUrl());
        assertEquals("A sunny day at the beach", memory.getDescription());
        assertEquals(LocalDate.of(2026, 4, 3), memory.getMemoryDate());
        assertEquals(now, memory.getCreatedAt());
    }

    @Test
    void testOnCreate_setsCreatedAtWhenNull() {
        Memory memory = new Memory();
        assertNull(memory.getCreatedAt());

        try {
            var method = Memory.class.getDeclaredMethod("onCreate");
            method.setAccessible(true);
            method.invoke(memory);
        } catch (Exception e) {
            fail("Failed to invoke onCreate: " + e.getMessage());
        }

        assertNotNull(memory.getCreatedAt());
    }

    @Test
    void testOnCreate_doesNotOverwriteExistingCreatedAt() {
        Memory memory = new Memory();
        Instant existing = Instant.parse("2025-06-15T12:00:00Z");
        memory.setCreatedAt(existing);

        try {
            var method = Memory.class.getDeclaredMethod("onCreate");
            method.setAccessible(true);
            method.invoke(memory);
        } catch (Exception e) {
            fail("Failed to invoke onCreate: " + e.getMessage());
        }

        assertEquals(existing, memory.getCreatedAt());
    }

    @Test
    void testDefaultConstructor() {
        Memory memory = new Memory();
        assertNull(memory.getId());
        assertNull(memory.getAlbum());
        assertNull(memory.getImageUrl());
        assertNull(memory.getDescription());
        assertNull(memory.getMemoryDate());
        assertNull(memory.getCreatedAt());
    }
}
