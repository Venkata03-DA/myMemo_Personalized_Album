package com.venkata.mymemo.service;

import com.venkata.mymemo.entity.Album;
import com.venkata.mymemo.repository.AlbumRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlbumService {

    private final AlbumRepository albumRepository; 
    // This is a dependency on the AlbumRepository, which will be used to perform database operations related to albums

    public AlbumService(AlbumRepository albumRepository) {  
        this.albumRepository = albumRepository;
    }
// And this is where the db query is written and repository jpa sees this and creates the query for us and we can use it in the controller to perform the operations.
    public Album createAlbum(Album album) { // Method to create a new album
        return albumRepository.save(album); // Save the album to the database and return the saved entity, which includes the generated ID and timestamps
    }

    public List<Album> getAllAlbums() { // Method to retrieve all albums from the database
        return albumRepository.findAll();
    }

    public Album getAlbumById(Long id) { // Method to retrieve a specific album by its unique identifier
        return albumRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Album not found with id: " + id)); 
    }

    public void deleteAlbum(Long id) {
        if (!albumRepository.existsById(id)) {
            throw new RuntimeException("Album not found with id: " + id);
        }
        albumRepository.deleteById(id);
    }
}
// In future we can add more methods like editing the album option where we can update the title, cover image and event date of the album.