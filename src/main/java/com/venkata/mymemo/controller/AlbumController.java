package com.venkata.mymemo.controller;

import com.venkata.mymemo.entity.Album;
import com.venkata.mymemo.service.AlbumService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/albums")
public class AlbumController {

    private final AlbumService albumService; 

    public AlbumController(AlbumService albumService) {
        this.albumService = albumService;
    }

    @PostMapping // Create a new album
    public ResponseEntity<Album> createAlbum(@RequestBody Album album) {
        return ResponseEntity.status(HttpStatus.CREATED).body(albumService.createAlbum(album));
    }

    @GetMapping
    public ResponseEntity<List<Album>> getAllAlbums() {
        return ResponseEntity.ok(albumService.getAllAlbums());
    }

    @GetMapping("/{albumId}")
    public ResponseEntity<Album> getAlbumById(@PathVariable("albumId") Long albumId) {
        return ResponseEntity.ok(albumService.getAlbumById(albumId));
    }

    @DeleteMapping("/{albumId}")
    public ResponseEntity<Void> deleteAlbum(@PathVariable("albumId") Long albumId) {
        albumService.deleteAlbum(albumId);
        return ResponseEntity.noContent().build();
    }
}
