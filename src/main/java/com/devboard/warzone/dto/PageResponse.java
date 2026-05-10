package com.devboard.warzone.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PageResponse<T> {
    private List<T> data;
    private int limit;
    private int skip;
    private long total;
}