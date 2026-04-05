package com.venkata.mymemo.repository;

import com.venkata.mymemo.entity.Album;
import com.venkata.mymemo.entity.Memory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class MemoryRepositoryTest {

    @Autowired
    private MemoryRepository memoryRepository;

    @Autowired
    private AlbumRepository albumRepository;

    private Album createAndSaveAlbum(String title) {
        Album album = new Album();
        album.setTitle(title);
        return albumRepository.save(album);
    }

    @Test
    void save_persistsMemory() {
        Album album = createAndSaveAlbum("Test Album");

        Memory memory = new Memory();
        memory.setDescription("A beautiful moment");
        memory.setAlbum(album);

        Memory saved = memoryRepository.save(memory);

        assertNotNull(saved.getId());
        assertEquals("A beautiful moment", saved.getDescription());
    }

    @Test
    void findByAlbumId_returnsMemoriesForAlbum() {
        Album album = createAndSaveAlbum("My Album");

        Memory m1 = new Memory();
        m1.setDescription("Memory 1");
        m1.setAlbum(album);
        m1.setMemoryDate(LocalDate.of(2026, 1, 1));

        Memory m2 = new Memory();
        m2.setDescription("Memory 2");
        m2.setAlbum(album);
        m2.setMemoryDate(LocalDate.of(2026, 2, 1));

        memoryRepository.save(m1);
        memoryRepository.save(m2);

        List<Memory> result = memoryRepository.findByAlbumId(album.getId());

        assertEquals(2, result.size());
    }

    @Test
    void findByAlbumId_doesNotReturnMemoriesFromOtherAlbum() {
        Album album1 = createAndSaveAlbum("Album One");
        Album album2 = createAndSaveAlbum("Album Two");

        Memory m1 = new Memory();
        m1.setDescription("Belongs to Album One");
        m1.setAlbum(album1);
        memoryRepository.save(m1);

        List<Memory> result = memoryRepository.findByAlbumId(album2.getId());

        assertTrue(result.isEmpty());
    }

    @Test
    void findByAlbumId_returnsEmptyListWhenNoMemories() {
        Album album = createAndSaveAlbum("Empty Album");

        List<Memory> result = memoryRepository.findByAlbumId(album.getId());

        assertTrue(result.isEmpty());
    }
}
