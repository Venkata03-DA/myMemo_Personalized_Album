package com.venkata.mymemo.controller;

import com.venkata.mymemo.entity.Memory;
import com.venkata.mymemo.service.MemoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/albums/{albumId}/memories")
public class MemoryController {

    private final MemoryService memoryService;

    public MemoryController(MemoryService memoryService) {
        this.memoryService = memoryService;
    }

    @PostMapping
    public ResponseEntity<Memory> createMemory(@PathVariable("albumId") Long albumId, @RequestBody Memory memory) {
        return ResponseEntity.status(HttpStatus.CREATED).body(memoryService.createMemory(albumId, memory));
    }

    @GetMapping
    public ResponseEntity<List<Memory>> getMemoriesByAlbum(@PathVariable Long albumId) {
        return ResponseEntity.ok(memoryService.getMemoriesByAlbumId(albumId));
    }
}
