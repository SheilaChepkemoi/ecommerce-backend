package com.sheilac.ecommerce.category.controller;

import com.sheilac.ecommerce.category.dto.CategoryRequest;
import com.sheilac.ecommerce.category.dto.CategoryResponse;
import com.sheilac.ecommerce.category.service.CategoryService;
import com.sheilac.ecommerce.utils.enums.ResponseType;
import com.sheilac.ecommerce.utils.response.CustomResponse;
import com.sheilac.ecommerce.utils.response.PageResponse;
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
    public ResponseEntity<CustomResponse<PageResponse<CategoryResponse>>> getAllCategories(
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortDirection
    ) {

        PageResponse<CategoryResponse> categories =
                categoryService.getAllCategories(
                        name,
                        page,
                        size,
                        sortBy,
                        sortDirection
                );

        CustomResponse<PageResponse<CategoryResponse>> response =
                CustomResponse
                        .<PageResponse<CategoryResponse>>builder()
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
                        .message("Category deleted successfully")
                        .messageTracker(System.currentTimeMillis())
                        .build();

        return ResponseEntity.ok(response);
    }


    

    @PatchMapping("/{id}/activate")
    public ResponseEntity<CustomResponse<Void>> activateCategory(
            @PathVariable Long id
    ) {

        categoryService.activateCategory(id);

        CustomResponse<Void> response =
                CustomResponse.<Void>builder()
                        .responseType(ResponseType.SUCCESS)
                        .message("Category activated successfully")
                        .build();

        return ResponseEntity.ok(response);
    }


}
