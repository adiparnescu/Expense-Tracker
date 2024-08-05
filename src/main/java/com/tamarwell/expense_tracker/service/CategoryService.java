package com.tamarwell.expense_tracker.service;

import com.tamarwell.expense_tracker.entity.Category;
import com.tamarwell.expense_tracker.entity.Subcategory;
import com.tamarwell.expense_tracker.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository repository;

    public Optional<Category> findCategoryByName(String categoryName){
        return repository.findCategoryByName(categoryName);
    }
}
