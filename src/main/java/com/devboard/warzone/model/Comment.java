package com.devboard.warzone.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Comment {
    private Long id;
    private Long postId;
    private String author;
    private String body;
    private LocalDateTime createdAt;
}