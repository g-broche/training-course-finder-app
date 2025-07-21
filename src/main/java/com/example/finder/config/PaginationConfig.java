package com.example.finder.config;

import org.springframework.stereotype.Component;

@Component
public class PaginationConfig {
    private final int maxResultsPerPage = 50;

    public int getMaxResultsPerPage() {
        return maxResultsPerPage;
    }
}
