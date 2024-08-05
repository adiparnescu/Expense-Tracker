package com.tamarwell.expense_tracker.repository;

import com.tamarwell.expense_tracker.entity.Category;
import com.tamarwell.expense_tracker.entity.Subcategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubcategoryRepository extends JpaRepository<Subcategory, Long> {
    Optional<Subcategory> findSubcategoryByName(String subcategoryName);
    Optional<Subcategory> findByNameAndCategoryId(String name, Long categoryId);
}
