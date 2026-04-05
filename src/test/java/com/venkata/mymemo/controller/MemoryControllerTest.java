package com.venkata.mymemo.controller;

import tools.jackson.databind.ObjectMapper;
import com.venkata.mymemo.entity.Memory;
import com.venkata.mymemo.service.MemoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MemoryController.class)
class MemoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MemoryService memoryService;

    @Test
    void createMemory_returns201WithCreatedMemory() throws Exception {
        Memory memory = new Memory();
        memory.setDescription("Sunset at the beach");
        memory.setMemoryDate(LocalDate.of(2026, 4, 3));

        Memory saved = new Memory();
        saved.setId(10L);
        saved.setDescription("Sunset at the beach");
        saved.setMemoryDate(LocalDate.of(2026, 4, 3));

        when(memoryService.createMemory(eq(1L), any(Memory.class))).thenReturn(saved);

        mockMvc.perform(post("/api/albums/1/memories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memory)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.description").value("Sunset at the beach"));
    }

    @Test
    void getMemoriesByAlbum_returns200WithList() throws Exception {
        Memory m1 = new Memory();
        m1.setId(1L);
        m1.setDescription("Memory One");

        Memory m2 = new Memory();
        m2.setId(2L);
        m2.setDescription("Memory Two");

        when(memoryService.getMemoriesByAlbumId(1L)).thenReturn(List.of(m1, m2));

        mockMvc.perform(get("/api/albums/1/memories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].description").value("Memory One"))
                .andExpect(jsonPath("$[1].description").value("Memory Two"));
    }

    @Test
    void getMemoriesByAlbum_returnsEmptyList() throws Exception {
        when(memoryService.getMemoriesByAlbumId(1L)).thenReturn(List.of());

        mockMvc.perform(get("/api/albums/1/memories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
