package com.example.controller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.testcontainers.containers.PostgreSQLContainer;

import com.example.UserApplication;
import com.example.dto.CreateUserRequest;
import com.example.entity.User;
import com.example.repository.UserRepository;

import tools.jackson.databind.ObjectMapper;

@SpringBootTest(classes = UserApplication.class)
@AutoConfigureMockMvc
public class UserControlerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;
    
    @SuppressWarnings("resource")
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @BeforeAll
    static void beforeAll() {
        postgres.start();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Test
    void shouldCreateUser() throws Exception {
        CreateUserRequest request = new CreateUserRequest("Test1", "test1@test.com", 21);
        
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value("Test1"))
            .andExpect(jsonPath("$.email").value("test1@test.com"))
            .andExpect(jsonPath("$.age").value(21));
    }

    @Test
    void shouldGetUserById() throws Exception {
        User user = userRepository.save(new User("Test2", "test2@test.com", 22));
        
        mockMvc.perform(get("/api/users/{id}", user.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Test2"))
            .andExpect(jsonPath("$.email").value("test2@test.com"))
            .andExpect(jsonPath("$.age").value(22));
    }
    
    @Test
    void shouldReturn404ForNonExistentUser() throws Exception {
        mockMvc.perform(get("/api/users/{id}", 9999L))
            .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeleteUser() throws Exception {
        User user = userRepository.save(new User("Test3", "test3@test.com", 23));
        
        mockMvc.perform(delete("/api/users/{id}", user.getId()))
            .andExpect(status().isNoContent());
        
        mockMvc.perform(get("/api/users/{id}", user.getId()))
            .andExpect(status().isNotFound());
    }

    @Test
    void shouldUpdateUser() throws Exception {
        User user = userRepository.save(new User("Test4", "test4@test.com", 24));
        CreateUserRequest request = new CreateUserRequest("UpdateTest4", "updated@test.com", 25);

        mockMvc.perform(put("/api/users/{id}", user.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("UpdateTest4"))
            .andExpect(jsonPath("$.email").value("updated@test.com"))
            .andExpect(jsonPath("$.age").value(25));
    }
}
