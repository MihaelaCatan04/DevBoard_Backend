package com.devboard.warzone.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PostRequest {

    @NotBlank(message = "Title is required")
    @Size(min = 5, max = 255, message = "Title must be 5–255 characters")
    private String title;

    @NotBlank(message = "Body is required")
    @Size(min = 10, message = "Body must be at least 10 characters")
    private String body;

    @Size(max = 50, message = "Tag must be under 50 characters")
    private String tag;
}