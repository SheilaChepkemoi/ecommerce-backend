package com.sheilac.ecommerce.product.service;

import com.sheilac.ecommerce.product.dto.ProductFilterRequest;
import com.sheilac.ecommerce.product.dto.ProductRequest;
import com.sheilac.ecommerce.product.dto.ProductResponse;
import com.sheilac.ecommerce.product.dto.UpdateProductRequest;
import com.sheilac.ecommerce.utils.response.PageResponse;

public interface ProductService {

    ProductResponse createProduct(ProductRequest request);

    PageResponse<ProductResponse> getAllProducts(
            ProductFilterRequest filter, int page, Integer size, String sortBy, String sortDirection);

    ProductResponse getProductById(Long id);

    ProductResponse updateProduct(Long id, UpdateProductRequest request);

    void deleteProduct(Long id);

    void activateProduct(Long id);
}