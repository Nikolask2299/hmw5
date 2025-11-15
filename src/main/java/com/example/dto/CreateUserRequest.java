package com.example.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;


public record CreateUserRequest(
    @NotBlank String name, 
    @NotBlank @Email String email,
    @NotNull @PositiveOrZero Integer age
) {}