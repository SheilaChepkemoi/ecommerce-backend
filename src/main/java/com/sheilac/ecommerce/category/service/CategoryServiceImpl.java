package com.sheilac.ecommerce.category.service;

import com.sheilac.ecommerce.category.dto.CategoryRequest;
import com.sheilac.ecommerce.category.dto.CategoryResponse;
import com.sheilac.ecommerce.category.entity.Category;
import com.sheilac.ecommerce.category.mapper.CategoryMapper;
import com.sheilac.ecommerce.category.repository.CategoryRepository;
import com.sheilac.ecommerce.product.repository.ProductRepository;
import com.sheilac.ecommerce.utils.config.PaginationConfig;
import com.sheilac.ecommerce.utils.enums.ErrorCode;
import com.sheilac.ecommerce.utils.exceptions.ApiException;
import com.sheilac.ecommerce.utils.response.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    private final CategoryMapper categoryMapper;

    private final ProductRepository productRepository;

    private final PaginationConfig paginationConfig;

    private static final Set<String> SORT_FIELDS =
            Set.of("name", "createdAt", "updatedAt");


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
    @Transactional(readOnly = true)
    public PageResponse<CategoryResponse> getAllCategories(
            String name,
            int page,
            Integer size,
            String sortBy,
            String sortDirection
    ) {

        Pageable pageable = paginationConfig.createPageable(
                page,
                size,
                sortBy,
                sortDirection,
                SORT_FIELDS
        );

        Page<Category> categoryPage;

        if (name != null && !name.isBlank()) {

            categoryPage =
                    categoryRepository
                            .findAllByNameContainingIgnoreCaseAndActiveTrue(
                                    name,
                                    pageable
                            );

        } else {

            categoryPage =
                    categoryRepository.findAllByActiveTrue(
                            pageable
                    );
        }

        return PageResponse.from(
                categoryPage,
                categoryMapper::toResponse
        );
    }


    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Long id) {

        Category category =
                categoryRepository.findByIdAndActiveTrue(id)
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

        if (!category.getActive()) {
            throw new ApiException(
                    "Category is already inactive",
                    ErrorCode.BAD_REQUEST
            );
        }

        if (productRepository.existsByCategoryIdAndActiveTrue(id)) {
            throw new ApiException(
                    "Category cannot be deleted because it is associated with active products",
                    ErrorCode.BAD_REQUEST
            );
        }

        category.setActive(false);

        categoryRepository.save(category);
    }

    @Override
    public void activateCategory(Long id){
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

        if (category.getActive()) {
            throw new ApiException(
                    "Category is already active",
                    ErrorCode.BAD_REQUEST
            );
        }

        category.setActive(true);

        categoryRepository.save(category);

    }
}

