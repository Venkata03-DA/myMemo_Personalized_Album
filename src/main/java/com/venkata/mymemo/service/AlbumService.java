package com.venkata.mymemo.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.venkata.mymemo.entity.Album;
import com.venkata.mymemo.repository.AlbumRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;

@Slf4j
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
        log.info("Creating album: {}", album.getTitle());
        Album saved = albumRepository.save(album);
        log.info("Album created successfully with id: {}", saved.getId());
        return saved;
    }

    public List<Album> getAllAlbums() {
        log.info("Fetching all albums");
        List<Album> albums = albumRepository.findAll();
        log.info("Found {} albums", albums.size());
        return albums;
    }

    public Album getAlbumById(Long id) {
        log.info("Fetching album with id: {}", id);
        Album album = albumRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Album not found with id: {}", id);
                    return new RuntimeException("Album not found with id: " + id);
                });
        log.info("Found album: {}", album.getTitle());
        return album;
    }

    public void deleteAlbum(Long id) {
        log.info("Deleting album with id: {}", id);
        Album album = albumRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Album not found with id: {}", id);
                    return new RuntimeException("Album not found with id: " + id);
                });

        if (album.getCoverImagePublicId() != null) {
            log.info("Deleting cover image from Cloudinary: {}", album.getCoverImagePublicId());
            try {
                cloudinary.uploader().destroy(album.getCoverImagePublicId(), ObjectUtils.emptyMap());
                log.info("Cover image deleted from Cloudinary successfully");
            } catch (IOException e) {
                log.error("Failed to delete image from Cloudinary: {}", e.getMessage(), e);
            }
        }

        albumRepository.deleteById(id);
        log.info("Album deleted successfully with id: {}", id);
    }
}