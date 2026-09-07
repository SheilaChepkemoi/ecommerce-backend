package com.sheilac.ecommerce.category.service;

import com.sheilac.ecommerce.category.dto.CategoryRequest;
import com.sheilac.ecommerce.category.dto.CategoryResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CategoryService {

    CategoryResponse createCategory(
            CategoryRequest request
    );

    List<CategoryResponse> getAllCategories();

    CategoryResponse getCategoryById(Long id);

    CategoryResponse updateCategory(
            Long id,
            CategoryRequest request
    );

    void deleteCategory(Long id);
}
