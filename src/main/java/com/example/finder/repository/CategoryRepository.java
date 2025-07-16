package com.example.finder.repository;

import com.example.finder.exception.RecordStatusNotFoundException;
import com.example.finder.model.Category;
import com.example.finder.model.RecordStatus;
import com.example.finder.model.enums.AvailableRecordStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByName(String categoryName);
}
