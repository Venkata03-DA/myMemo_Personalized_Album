package com.venkata.mymemo.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.venkata.mymemo.entity.Album;
import com.venkata.mymemo.repository.AlbumRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class AlbumService {

    private final AlbumRepository albumRepository;
    private final Cloudinary cloudinary;

    public AlbumService(
            AlbumRepository albumRepository,
            @Value("${cloudinary.cloud-name}") String cloudName,
            @Value("${cloudinary.api-key}")    String apiKey,
            @Value("${cloudinary.api-secret}") String apiSecret) {
        this.albumRepository = albumRepository;
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key",    apiKey,
                "api_secret", apiSecret,
                "secure",     true
        ));
    }

    public Album createAlbum(Album album) {
        return albumRepository.save(album);
    }

    public List<Album> getAllAlbums() {
        return albumRepository.findAll();
    }

    public Album getAlbumById(Long id) {
        return albumRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Album not found with id: " + id));
    }

    public void deleteAlbum(Long id) {
        Album album = albumRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Album not found with id: " + id));

        if (album.getCoverImagePublicId() != null) {
            try {
                cloudinary.uploader().destroy(album.getCoverImagePublicId(), ObjectUtils.emptyMap());
            } catch (IOException e) {
                // Log but don't block deletion if Cloudinary call fails
                System.err.println("Failed to delete image from Cloudinary: " + e.getMessage());
            }
        }

        albumRepository.deleteById(id);
    }
}