package com.example.finder.seeders;

import com.example.finder.model.Category;
import com.example.finder.model.UserStatus;
import com.example.finder.model.enums.AvailableUserStatus;
import com.example.finder.repository.CategoryRepository;
import com.example.finder.repository.UserStatusRepository;
import org.springframework.stereotype.Component;

@Component
public class CategorySeeder {
    private final CategoryRepository categoryRepository;
    private final String[] defaultCategories = new String[]{
            "wallet",
            "keys",
            "phone",
            "clothes",
            "bag",
            "book",
            "misc"
    };

    public String[] getDefaultCategories(){
        return this.defaultCategories;
    }

    public CategorySeeder(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public void fillDefaultCategories(){
        for (String categoryName : defaultCategories) {
            categoryRepository.findByName(categoryName)
                    .orElseGet(() -> categoryRepository.save(new Category(categoryName)));
        }
    }
}

