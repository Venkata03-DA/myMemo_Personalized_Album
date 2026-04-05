package com.venkata.mymemo.repository;

import com.venkata.mymemo.entity.Album;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlbumRepository extends JpaRepository<Album, Long> {
}
