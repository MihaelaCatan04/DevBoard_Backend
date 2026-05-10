package com.devboard.warzone.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CommentRequest {

    @NotBlank(message = "Comment body is required")
    @Size(min = 1, max = 1000, message = "Comment must be 1–1000 characters")
    private String body;
}