package com.sheilac.ecommerce.category.service;

import com.sheilac.ecommerce.category.dto.CategoryRequest;
import com.sheilac.ecommerce.category.dto.CategoryResponse;
import com.sheilac.ecommerce.category.entity.Category;
import com.sheilac.ecommerce.category.mapper.CategoryMapper;
import com.sheilac.ecommerce.category.repository.CategoryRepository;
import com.sheilac.ecommerce.utils.enums.ErrorCode;
import com.sheilac.ecommerce.utils.exceptions.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    private final CategoryMapper categoryMapper;


    @Override
    public CategoryResponse createCategory(
            CategoryRequest request
    ) {

        if (categoryRepository.existsByNameIgnoreCase(
                request.getName()
        )) {

            throw new ApiException(
                    "Category with name "
                            + request.getName()
                            + " already exists",
                    ErrorCode.CONFLICT
            );
        }

        Category category =
                categoryMapper.toEntity(request);

        Category savedCategory =
                categoryRepository.save(category);

        return categoryMapper.toResponse(
                savedCategory
        );
    }

    @Override
    public List<CategoryResponse> getAllCategories() {

        return categoryRepository.findAll()
                .stream()
                .map(categoryMapper::toResponse)
                .toList();
    }


    @Override
    public CategoryResponse getCategoryById(Long id) {

        Category category =
                categoryRepository.findById(id)
                        .orElseThrow(() ->
                                new ApiException(
                                        "Category with ID "
                                                + id
                                                + " not found",
                                        ErrorCode.NOT_FOUND
                                )
                        );

        return categoryMapper.toResponse(
                category
        );
    }

    @Override
    public CategoryResponse updateCategory(
            Long id,
            CategoryRequest request
    ) {

        Category category =
                categoryRepository.findById(id)
                        .orElseThrow(() ->
                                new ApiException(
                                        "Category with ID "
                                                + id
                                                + " not found",
                                        ErrorCode.NOT_FOUND
                                )
                        );


        if (!category.getName().equalsIgnoreCase(
                request.getName()
        )
                && categoryRepository.existsByNameIgnoreCase(
                request.getName()
        )) {

            throw new ApiException(
                    "Category with name "
                            + request.getName()
                            + " already exists",
                    ErrorCode.CONFLICT
            );
        }
        categoryMapper.updateEntity(
                request,
                category
        );

        Category updatedCategory =
                categoryRepository.save(category);

        return categoryMapper.toResponse(
                updatedCategory
        );
    }

    @Override
    public void deleteCategory(Long id) {

        Category category =
                categoryRepository.findById(id)
                        .orElseThrow(() ->
                                new ApiException(
                                        "Category with ID "
                                                + id
                                                + " not found",
                                        ErrorCode.NOT_FOUND
                                )
                        );

        categoryRepository.delete(category);
    }
}

