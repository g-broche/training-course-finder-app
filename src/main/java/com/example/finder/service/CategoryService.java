package com.example.finder.service;

import com.example.finder.dto.output.CategoryDto;
import com.example.finder.model.Category;
import com.example.finder.repository.*;
import com.example.finder.response.ApiResponseFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryService(
            CategoryRepository categoryRepository
    ) {
        this.categoryRepository = categoryRepository;

    }
    public ResponseEntity<?> getAllAvailableCategories(){
        try {
            List<Category> categories = categoryRepository.findAll();
            List<CategoryDto> categoriesDto = categories.stream()
                    .map(Category::toDto)
                    .toList();
            return ApiResponseFactory.success(categoriesDto);
        } catch (Exception e) {
            return  ApiResponseFactory.internalError();
        }
    }
}
