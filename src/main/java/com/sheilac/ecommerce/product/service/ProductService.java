package com.sheilac.ecommerce.product.service;

import com.sheilac.ecommerce.product.dto.ProductFilterRequest;
import com.sheilac.ecommerce.product.dto.ProductRequest;
import com.sheilac.ecommerce.product.dto.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
public interface ProductService {

    ProductResponse createProduct(ProductRequest request);

    Page<ProductResponse> getAllProducts(
            ProductFilterRequest filter,
            Pageable pageable
    );

    ProductResponse getProductById(Long id);

    ProductResponse updateProduct(
            Long id,
            ProductRequest request
    );

    void deleteProduct(Long id);
}
