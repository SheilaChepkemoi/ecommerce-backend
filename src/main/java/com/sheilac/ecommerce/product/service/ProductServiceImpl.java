package com.sheilac.ecommerce.product.service;

import com.sheilac.ecommerce.category.entity.Category;
import com.sheilac.ecommerce.category.repository.CategoryRepository;
import com.sheilac.ecommerce.product.dto.ProductFilterRequest;
import com.sheilac.ecommerce.product.dto.ProductRequest;
import com.sheilac.ecommerce.product.dto.ProductResponse;
import com.sheilac.ecommerce.product.entity.Product;
import com.sheilac.ecommerce.product.mapper.ProductMapper;
import com.sheilac.ecommerce.product.repository.ProductRepository;
import com.sheilac.ecommerce.product.specification.ProductSpecification;
import com.sheilac.ecommerce.utils.enums.ErrorCode;
import com.sheilac.ecommerce.utils.exceptions.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService{

    private final ProductMapper productMapper;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public ProductResponse createProduct(
            ProductRequest request
    ) {

        if (productRepository.existsBySku(request.getSku())) {

            throw new ApiException(
                    "Product with SKU "
                            + request.getSku()
                            + " already exists",
                    ErrorCode.CONFLICT
            );
        }


        Category category =
                categoryRepository.findById(
                                request.getCategoryId()
                        )
                        .orElseThrow(() ->
                                new ApiException(
                                        "Category with ID "
                                                + request.getCategoryId()
                                                + " not found",
                                        ErrorCode.NOT_FOUND
                                )
                        );


        Product product =
                productMapper.toEntity(request);


        product.setCategory(category);


        Product savedProduct =
                productRepository.save(product);


        return productMapper.toResponse(
                savedProduct
        );
    }


    @Override
    public Page<ProductResponse> getAllProducts(
            ProductFilterRequest filter,
            Pageable pageable
    ) {

        List<Specification<Product>> specifications = new ArrayList<>();

        if (filter.getSearch() != null
                && !filter.getSearch().isBlank()) {

            specifications.add(
                    ProductSpecification.search(
                            filter.getSearch()
                    )
            );
        }

        if (filter.getBrand() != null
                && !filter.getBrand().isBlank()) {

            specifications.add(
                    ProductSpecification.hasBrand(
                            filter.getBrand()
                    )
            );
        }

        if (filter.getMinPrice() != null) {

            specifications.add(
                    ProductSpecification.priceGreaterThanOrEqualTo(
                            filter.getMinPrice()
                    )
            );
        }

        if (filter.getMaxPrice() != null) {

            specifications.add(
                    ProductSpecification.priceLessThanOrEqualTo(
                            filter.getMaxPrice()
                    )
            );
        }

        if (filter.getActive() != null) {

            specifications.add(
                    ProductSpecification.hasActiveStatus(
                            filter.getActive()
                    )
            );
        }

        Specification<Product> specification =
                Specification.allOf(specifications);

        Page<Product> products =
                productRepository.findAll(
                        specification,
                        pageable
                );

        return products.map(productMapper::toResponse);
    }


    @Override
    public ProductResponse getProductById(Long id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new ApiException(
                                "Product with ID " + id + " not found",
                                ErrorCode.NOT_FOUND
                        )
                );

            return productMapper.toResponse(product);
    }

    @Override
    public ProductResponse updateProduct(
            Long id,
            ProductRequest request
    ) {

        Product product =
                productRepository.findById(id)
                        .orElseThrow(() ->
                                new ApiException(
                                        "Product with ID "
                                                + id
                                                + " not found",
                                        ErrorCode.NOT_FOUND
                                )
                        );


        if (!product.getSku().equals(request.getSku())
                && productRepository.existsBySku(
                request.getSku()
        )) {

            throw new ApiException(
                    "Product with SKU "
                            + request.getSku()
                            + " already exists",
                    ErrorCode.CONFLICT
            );
        }


        Category category =
                categoryRepository.findById(
                                request.getCategoryId()
                        )
                        .orElseThrow(() ->
                                new ApiException(
                                        "Category with ID "
                                                + request.getCategoryId()
                                                + " not found",
                                        ErrorCode.NOT_FOUND
                                )
                        );


        productMapper.updateEntity(
                request,
                product
        );


        product.setCategory(category);


        Product updatedProduct =
                productRepository.save(product);


        return productMapper.toResponse(
                updatedProduct
        );
    }

    @Override
    public void deleteProduct(Long id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new ApiException(
                                "Product with ID "
                                        + id
                                        + " not found",
                                ErrorCode.NOT_FOUND
                        )
                );

        productRepository.delete(product);
    }
}
