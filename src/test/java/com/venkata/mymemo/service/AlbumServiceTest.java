package com.venkata.mymemo.service;

import com.venkata.mymemo.entity.Album;
import com.venkata.mymemo.repository.AlbumRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlbumServiceTest {

    @Mock
    private AlbumRepository albumRepository;

    @InjectMocks
    private AlbumService albumService;

    @Test
    void createAlbum_savesAndReturnsAlbum() {
        Album album = new Album();
        album.setTitle("New Album");

        Album saved = new Album();
        saved.setId(1L);
        saved.setTitle("New Album");

        when(albumRepository.save(album)).thenReturn(saved);

        Album result = albumService.createAlbum(album);

        assertEquals(1L, result.getId());
        assertEquals("New Album", result.getTitle());
        verify(albumRepository, times(1)).save(album);
    }

    @Test
    void getAllAlbums_returnsAllAlbums() {
        Album a1 = new Album();
        a1.setId(1L);
        Album a2 = new Album();
        a2.setId(2L);

        when(albumRepository.findAll()).thenReturn(List.of(a1, a2));

        List<Album> result = albumService.getAllAlbums();

        assertEquals(2, result.size());
        verify(albumRepository, times(1)).findAll();
    }

    @Test
    void getAlbumById_returnsAlbumWhenFound() {
        Album album = new Album();
        album.setId(1L);
        album.setTitle("Found Album");

        when(albumRepository.findById(1L)).thenReturn(Optional.of(album));

        Album result = albumService.getAlbumById(1L);

        assertEquals(1L, result.getId());
        assertEquals("Found Album", result.getTitle());
    }

    @Test
    void getAlbumById_throwsExceptionWhenNotFound() {
        when(albumRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> albumService.getAlbumById(99L));

        assertEquals("Album not found with id: 99", ex.getMessage());
    }
}
