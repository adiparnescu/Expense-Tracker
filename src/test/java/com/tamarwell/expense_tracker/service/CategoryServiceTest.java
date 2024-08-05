package com.tamarwell.expense_tracker.service;

import com.tamarwell.expense_tracker.entity.Category;
import com.tamarwell.expense_tracker.repository.CategoryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

public class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    @BeforeEach
    public void setup(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testFindCategoryByNameFound(){
        String categoryName = "Food";

        Category category = new Category();
        category.setName(categoryName);

        when(categoryRepository.findCategoryByName(categoryName)).thenReturn(Optional.of(category));

        Optional<Category> result = categoryService.findCategoryByName(categoryName);

        assertTrue(result.isPresent());
        assertEquals(result.get().getName(), categoryName);
    }

    @Test
    public void testFindCategoryByNameNotFound(){
        String categoryName= "Food";

        when(categoryRepository.findCategoryByName(categoryName)).thenReturn(Optional.empty());

        Optional<Category> result = categoryService.findCategoryByName(categoryName);

        assertTrue(result.isEmpty());
    }

}
