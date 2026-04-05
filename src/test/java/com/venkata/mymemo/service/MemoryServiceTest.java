package com.venkata.mymemo.service;

import com.venkata.mymemo.entity.Album;
import com.venkata.mymemo.entity.Memory;
import com.venkata.mymemo.repository.AlbumRepository;
import com.venkata.mymemo.repository.MemoryRepository;
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
class MemoryServiceTest {

    @Mock
    private MemoryRepository memoryRepository;

    @Mock
    private AlbumRepository albumRepository;

    @InjectMocks
    private MemoryService memoryService;

    @Test
    void createMemory_setsAlbumAndSaves() {
        Album album = new Album();
        album.setId(1L);
        album.setTitle("My Album");

        Memory memory = new Memory();
        memory.setDescription("A great day");

        Memory saved = new Memory();
        saved.setId(10L);
        saved.setDescription("A great day");
        saved.setAlbum(album);

        when(albumRepository.findById(1L)).thenReturn(Optional.of(album));
        when(memoryRepository.save(memory)).thenReturn(saved);

        Memory result = memoryService.createMemory(1L, memory);

        assertEquals(10L, result.getId());
        assertEquals(album, memory.getAlbum());
        verify(memoryRepository, times(1)).save(memory);
    }

    @Test
    void createMemory_throwsExceptionWhenAlbumNotFound() {
        Memory memory = new Memory();
        memory.setDescription("Some memory");

        when(albumRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> memoryService.createMemory(99L, memory));

        assertEquals("Album not found with id: 99", ex.getMessage());
        verify(memoryRepository, never()).save(any());
    }

    @Test
    void getMemoriesByAlbumId_returnsMemoriesForAlbum() {
        Memory m1 = new Memory();
        m1.setId(1L);
        Memory m2 = new Memory();
        m2.setId(2L);

        when(memoryRepository.findByAlbumId(1L)).thenReturn(List.of(m1, m2));

        List<Memory> result = memoryService.getMemoriesByAlbumId(1L);

        assertEquals(2, result.size());
        verify(memoryRepository, times(1)).findByAlbumId(1L);
    }

    @Test
    void getMemoriesByAlbumId_returnsEmptyListWhenNoMemories() {
        when(memoryRepository.findByAlbumId(1L)).thenReturn(List.of());

        List<Memory> result = memoryService.getMemoriesByAlbumId(1L);

        assertTrue(result.isEmpty());
    }
}
