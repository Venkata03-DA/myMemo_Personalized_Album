package com.venkata.mymemo.controller;

import tools.jackson.databind.ObjectMapper;
import com.venkata.mymemo.entity.Album;
import com.venkata.mymemo.service.AlbumService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AlbumController.class)
class AlbumControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AlbumService albumService;

    @Test
    void createAlbum_returns201WithCreatedAlbum() throws Exception {
        Album album = new Album();
        album.setTitle("Summer Trip");
        album.setEventDate(LocalDate.of(2026, 4, 3));

        Album saved = new Album();
        saved.setId(1L);
        saved.setTitle("Summer Trip");
        saved.setEventDate(LocalDate.of(2026, 4, 3));

        when(albumService.createAlbum(any(Album.class))).thenReturn(saved);

        mockMvc.perform(post("/api/albums")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(album)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Summer Trip"));
    }

    @Test
    void getAllAlbums_returns200WithList() throws Exception {
        Album a1 = new Album();
        a1.setId(1L);
        a1.setTitle("Album One");

        Album a2 = new Album();
        a2.setId(2L);
        a2.setTitle("Album Two");

        when(albumService.getAllAlbums()).thenReturn(List.of(a1, a2));

        mockMvc.perform(get("/api/albums"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("Album One"))
                .andExpect(jsonPath("$[1].title").value("Album Two"));
    }

    @Test
    void getAllAlbums_returnsEmptyList() throws Exception {
        when(albumService.getAllAlbums()).thenReturn(List.of());

        mockMvc.perform(get("/api/albums"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getAlbumById_returns200WithAlbum() throws Exception {
        Album album = new Album();
        album.setId(1L);
        album.setTitle("My Album");

        when(albumService.getAlbumById(1L)).thenReturn(album);

        mockMvc.perform(get("/api/albums/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("My Album"));
    }
}
