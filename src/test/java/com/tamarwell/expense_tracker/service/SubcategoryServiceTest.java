package com.tamarwell.expense_tracker.service;

import com.tamarwell.expense_tracker.entity.Category;
import com.tamarwell.expense_tracker.entity.Subcategory;
import com.tamarwell.expense_tracker.repository.SubcategoryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class SubcategoryServiceTest {

    @Mock
    private SubcategoryRepository subcategoryRepository;

    @InjectMocks
    private SubcategoryService subcategoryService;

    @BeforeEach
    public void setup(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testFindByNameAndCategoryIdFound(){

        String subcategoryName = "Food";
        Long categoryId = 1L;
        Subcategory subcategory = new Subcategory();

        Category category = new Category();
        category.setId(categoryId);

        subcategory.setCategory(category);
        subcategory.setName(subcategoryName);

        when(subcategoryRepository.findByNameAndCategoryId(subcategoryName, categoryId)).thenReturn(Optional.of(subcategory));

        Optional<Subcategory> result = subcategoryRepository.findByNameAndCategoryId(subcategoryName, categoryId);

        assertTrue(result.isPresent());
        assertEquals(subcategoryName, result.get().getName());
        assertEquals(categoryId, result.get().getCategory().getId());
    }

    @Test
    public void testFindByNameAndCategoryIdNotFound(){

        String subcategoryName = "Food";
        Long categoryId = 1L;

        when(subcategoryRepository.findByNameAndCategoryId(subcategoryName, categoryId)).thenReturn(Optional.empty());

        Optional<Subcategory> result = subcategoryRepository.findByNameAndCategoryId(subcategoryName, categoryId);

        assertFalse(result.isPresent());
    }
}
