package com.venkata.mymemo.repository;

import com.venkata.mymemo.entity.Album;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlbumRepository extends JpaRepository<Album, Long> { 
    // This interface extends JpaRepository, providing CRUD operations for Album entities with Long as the ID type
}
