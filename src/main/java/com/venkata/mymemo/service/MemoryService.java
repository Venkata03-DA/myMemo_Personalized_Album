package com.venkata.mymemo.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.venkata.mymemo.entity.Memory;
import com.venkata.mymemo.repository.AlbumRepository;
import com.venkata.mymemo.repository.MemoryRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class MemoryService {

    private final MemoryRepository memoryRepository;
    private final AlbumRepository albumRepository;
    private final Cloudinary cloudinary;

    public MemoryService(
            MemoryRepository memoryRepository,
            AlbumRepository albumRepository,
            @Value("${cloudinary.cloud-name}") String cloudName,
            @Value("${cloudinary.api-key}")    String apiKey,
            @Value("${cloudinary.api-secret}") String apiSecret) {
        this.memoryRepository = memoryRepository;
        this.albumRepository  = albumRepository;
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key",    apiKey,
                "api_secret", apiSecret,
                "secure",     true
        ));
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

        if (memory.getImagePublicId() != null) {
            try {
                cloudinary.uploader().destroy(memory.getImagePublicId(), ObjectUtils.emptyMap());
            } catch (IOException e) {
                System.err.println("Failed to delete image from Cloudinary: " + e.getMessage());
            }
        }

        memoryRepository.deleteById(memoryId);
    }
}
