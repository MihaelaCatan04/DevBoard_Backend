package com.devboard.warzone.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Post {
    private Long id;
    private String title;
    private String body;
    private String author;
    private String tag;
    private int votes;
    private LocalDateTime createdAt;
    private LocalDateTime deletedAt;
}