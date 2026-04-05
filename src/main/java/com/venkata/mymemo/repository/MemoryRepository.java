package com.venkata.mymemo.repository;

import com.venkata.mymemo.entity.Memory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MemoryRepository extends JpaRepository<Memory, Long> {

    List<Memory> findByAlbumId(Long albumId);
}
