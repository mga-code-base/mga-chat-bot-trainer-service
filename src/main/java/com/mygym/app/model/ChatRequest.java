package com.mygym.app.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ChatRequest(
        @NotBlank @Size(max = 2000) String message,
        @Pattern(regexp = "(?i)nice|rude") String mode,
        @NotBlank @Size(max = 100) String sessionId) {
}
