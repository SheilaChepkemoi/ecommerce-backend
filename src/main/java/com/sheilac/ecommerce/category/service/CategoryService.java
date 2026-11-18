package com.sheilac.ecommerce.category.service;

import com.sheilac.ecommerce.category.dto.CategoryRequest;
import com.sheilac.ecommerce.category.dto.CategoryResponse;
import com.sheilac.ecommerce.utils.response.PageResponse;
import org.springframework.stereotype.Service;

public interface CategoryService {

    CategoryResponse createCategory(
            CategoryRequest request
    );

    PageResponse<CategoryResponse> getAllCategories(
            String name,
            int page,
            Integer size,
            String sortBy,
            String sortDirection
    );

    CategoryResponse getCategoryById(Long id);

    CategoryResponse updateCategory(
            Long id,
            CategoryRequest request
    );

    void deleteCategory(Long id);

    void activateCategory(Long id);
}
