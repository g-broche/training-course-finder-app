package com.example.finder.dto.output;

import com.example.finder.model.Category;
import com.example.finder.model.Role;

import java.util.UUID;

public class CategoryDto {
    private Long id;
    private String name;

    public CategoryDto(Category category) {

        this.id = category.getId();
        this.name = category.getName();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
