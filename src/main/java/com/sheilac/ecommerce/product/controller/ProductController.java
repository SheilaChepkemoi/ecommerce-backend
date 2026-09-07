package com.sheilac.ecommerce.product.controller;

import com.sheilac.ecommerce.product.dto.ProductFilterRequest;
import com.sheilac.ecommerce.product.dto.ProductRequest;
import com.sheilac.ecommerce.product.dto.ProductResponse;
import com.sheilac.ecommerce.product.service.ProductService;
import com.sheilac.ecommerce.utils.enums.ResponseType;
import com.sheilac.ecommerce.utils.response.CustomResponse;
import com.sheilac.ecommerce.utils.response.PageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping()
    public ResponseEntity<CustomResponse<ProductResponse>> createProduct(
            @Valid @RequestBody ProductRequest request
    ) {

        ProductResponse product = productService.createProduct(request);

        CustomResponse<ProductResponse> response = CustomResponse
                .<ProductResponse>builder()
                .generatedId(product.getId())
                .generatedCode(product.getSku())
                .payload(product)
                .message("Product created successfully")
                .messageTracker(System.currentTimeMillis())
                .build();

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


    @GetMapping
    public ResponseEntity<
            CustomResponse<PageResponse<ProductResponse>>> getAllProducts(

            @ModelAttribute ProductFilterRequest filter,

            Pageable pageable
    ) {

        Page<ProductResponse> products =
                productService.getAllProducts(
                        filter,
                        pageable
                );

        PageResponse<ProductResponse> pageResponse =
                PageResponse.from(products);

        CustomResponse<PageResponse<ProductResponse>> response =
                CustomResponse
                        .<PageResponse<ProductResponse>>builder()
                        .responseType(ResponseType.SUCCESS)
                        .payload(pageResponse)
                        .message(
                                "Products retrieved successfully"
                        )
                        .messageTracker(
                                System.currentTimeMillis()
                        )
                        .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomResponse<ProductResponse>> getProductById(
            @PathVariable Long id
    ) {

        ProductResponse product =
                productService.getProductById(id);

        CustomResponse<ProductResponse> response =
                CustomResponse
                        .<ProductResponse>builder()
                        .responseType(ResponseType.SUCCESS)
                        .generatedId(product.getId())
                        .generatedCode(product.getSku())
                        .payload(product)
                        .message("Product retrieved successfully")
                        .messageTracker(System.currentTimeMillis())
                        .build();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomResponse<ProductResponse>> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request
    ) {

        ProductResponse product =
                productService.updateProduct(id, request);

        CustomResponse<ProductResponse> response =
                CustomResponse
                        .<ProductResponse>builder()
                        .responseType(ResponseType.SUCCESS)
                        .generatedId(product.getId())
                        .generatedCode(product.getSku())
                        .payload(product)
                        .message("Product updated successfully")
                        .messageTracker(System.currentTimeMillis())
                        .build();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable Long id
    ) {

        productService.deleteProduct(id);

        return ResponseEntity.noContent().build();
    }


    
}
