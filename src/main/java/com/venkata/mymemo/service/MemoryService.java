package com.venkata.mymemo.service;

import com.venkata.mymemo.entity.Memory;
import com.venkata.mymemo.repository.AlbumRepository;
import com.venkata.mymemo.repository.MemoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MemoryService {

    private final MemoryRepository memoryRepository;
    private final AlbumRepository albumRepository;

    public MemoryService(MemoryRepository memoryRepository, AlbumRepository albumRepository) {
        this.memoryRepository = memoryRepository;
        this.albumRepository = albumRepository;
    }

    public Memory createMemory(Long albumId, Memory memory) {
        var album = albumRepository.findById(albumId)
                .orElseThrow(() -> new RuntimeException("Album not found with id: " + albumId));
        memory.setAlbum(album);
        return memoryRepository.save(memory);
    }

    public List<Memory> getMemoriesByAlbumId(Long albumId) {
        return memoryRepository.findByAlbumId(albumId);
    }

    public void deleteMemory(Long albumId, Long memoryId) {
        Memory memory = memoryRepository.findById(memoryId)
                .orElseThrow(() -> new RuntimeException("Memory not found with id: " + memoryId));
        if (!memory.getAlbum().getId().equals(albumId)) {
            throw new RuntimeException("Memory does not belong to album with id: " + albumId);
        }
        memoryRepository.deleteById(memoryId);
    }
}
