package com.venkata.mymemo.repository;

import com.venkata.mymemo.entity.Album;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class AlbumRepositoryTest {

    @Autowired
    private AlbumRepository albumRepository;

    @Test
    void save_persistsAlbum() {
        Album album = new Album();
        album.setTitle("Holiday 2026");
        album.setEventDate(LocalDate.of(2026, 4, 3));

        Album saved = albumRepository.save(album);

        assertNotNull(saved.getId());
        assertEquals("Holiday 2026", saved.getTitle());
    }

    @Test
    void findById_returnsAlbumWhenExists() {
        Album album = new Album();
        album.setTitle("Find Me");
        Album saved = albumRepository.save(album);

        Optional<Album> found = albumRepository.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals("Find Me", found.get().getTitle());
    }

    @Test
    void findById_returnsEmptyWhenNotExists() {
        Optional<Album> found = albumRepository.findById(999L);

        assertFalse(found.isPresent());
    }

    @Test
    void findAll_returnsAllSavedAlbums() {
        Album a1 = new Album();
        a1.setTitle("Album A");
        Album a2 = new Album();
        a2.setTitle("Album B");

        albumRepository.save(a1);
        albumRepository.save(a2);

        List<Album> albums = albumRepository.findAll();

        assertTrue(albums.size() >= 2);
    }

    @Test
    void delete_removesAlbum() {
        Album album = new Album();
        album.setTitle("To Delete");
        Album saved = albumRepository.save(album);

        albumRepository.deleteById(saved.getId());

        assertFalse(albumRepository.findById(saved.getId()).isPresent());
    }
}
