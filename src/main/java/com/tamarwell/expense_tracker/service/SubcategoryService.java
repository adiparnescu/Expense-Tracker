package com.tamarwell.expense_tracker.service;

import com.tamarwell.expense_tracker.entity.Subcategory;
import com.tamarwell.expense_tracker.repository.SubcategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SubcategoryService {

    @Autowired
    private SubcategoryRepository repository;

    public Optional<Subcategory> findByNameAndCategoryId(String subcategoryName, Long categoryId){
        return repository.findByNameAndCategoryId(subcategoryName, categoryId);
    }
}
