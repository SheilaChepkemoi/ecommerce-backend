package com.sheilac.ecommerce.category.controller;

import com.sheilac.ecommerce.category.dto.CategoryRequest;
import com.sheilac.ecommerce.category.dto.CategoryResponse;
import com.sheilac.ecommerce.category.service.CategoryService;
import com.sheilac.ecommerce.utils.enums.ResponseType;
import com.sheilac.ecommerce.utils.response.CustomResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    // CREATE CATEGORY
    @PostMapping
    public ResponseEntity<CustomResponse<CategoryResponse>> createCategory(
            @Valid @RequestBody CategoryRequest request
    ) {

        CategoryResponse category =
                categoryService.createCategory(request);

        CustomResponse<CategoryResponse> response =
                CustomResponse
                        .<CategoryResponse>builder()
                        .responseType(ResponseType.SUCCESS)
                        .generatedId(category.getId())
                        .payload(category)
                        .message("Category created successfully")
                        .messageTracker(System.currentTimeMillis())
                        .build();

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


    // GET ALL CATEGORIES
    @GetMapping
    public ResponseEntity<CustomResponse<List<CategoryResponse>>> getAllCategories() {

        List<CategoryResponse> categories =
                categoryService.getAllCategories();

        CustomResponse<List<CategoryResponse>> response =
                CustomResponse
                        .<List<CategoryResponse>>builder()
                        .responseType(ResponseType.SUCCESS)
                        .payload(categories)
                        .message("Categories retrieved successfully")
                        .messageTracker(System.currentTimeMillis())
                        .build();

        return ResponseEntity.ok(response);
    }


    // GET CATEGORY BY ID
    @GetMapping("/{id}")
    public ResponseEntity<CustomResponse<CategoryResponse>> getCategoryById(
            @PathVariable Long id
    ) {

        CategoryResponse category =
                categoryService.getCategoryById(id);

        CustomResponse<CategoryResponse> response =
                CustomResponse
                        .<CategoryResponse>builder()
                        .responseType(ResponseType.SUCCESS)
                        .generatedId(category.getId())
                        .payload(category)
                        .message("Category retrieved successfully")
                        .messageTracker(System.currentTimeMillis())
                        .build();

        return ResponseEntity.ok(response);
    }


    // UPDATE CATEGORY
    @PutMapping("/{id}")
    public ResponseEntity<CustomResponse<CategoryResponse>> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequest request
    ) {

        CategoryResponse category =
                categoryService.updateCategory(
                        id,
                        request
                );

        CustomResponse<CategoryResponse> response =
                CustomResponse
                        .<CategoryResponse>builder()
                        .responseType(ResponseType.SUCCESS)
                        .generatedId(category.getId())
                        .payload(category)
                        .message("Category updated successfully")
                        .messageTracker(System.currentTimeMillis())
                        .build();

        return ResponseEntity.ok(response);
    }


    // DELETE CATEGORY
    @DeleteMapping("/{id}")
    public ResponseEntity<CustomResponse<Void>> deleteCategory(
            @PathVariable Long id
    ) {

        categoryService.deleteCategory(id);

        CustomResponse<Void> response =
                CustomResponse
                        .<Void>builder()
                        .responseType(ResponseType.SUCCESS)
                        .generatedId(id)
                        .message("Category deleted successfully")
                        .messageTracker(System.currentTimeMillis())
                        .build();

        return ResponseEntity.ok(response);
    }


}
