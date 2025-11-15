package com.example.dto;

import java.time.LocalDateTime;

import com.example.entity.User;

public record  UserDto (
    Long id,
    String name,
    String email,
    Integer age,
    LocalDateTime createdAt
) {

    public UserDto(User savedUser) {
        this(
            savedUser.getId(),
            savedUser.getName(),
            savedUser.getEmail(),
            savedUser.getAge(),
            savedUser.getCreatedAt()
        );
    }}
