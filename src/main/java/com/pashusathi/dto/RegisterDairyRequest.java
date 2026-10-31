package com.pashusathi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterDairyRequest(
        @NotBlank @Size(max = 150) String dairyName,
        @Size(max = 255) String address,
        @Size(max = 20) String contactPhone,
        @NotBlank @Size(max = 120) String fullName,
        @NotBlank
        @Pattern(regexp = "^[A-Za-z0-9._-]{4,60}$",
                message = "Username must be 4-60 characters: letters, digits, dot, underscore or hyphen")
        String username,
        @NotBlank
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).{8,72}$",
                message = "Password must be 8-72 characters and contain a letter and a digit")
        String password) { }