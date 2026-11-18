package com.sheilac.ecommerce.product.service;

import com.sheilac.ecommerce.brand.entity.Brand;
import com.sheilac.ecommerce.brand.repository.BrandRepository;
import com.sheilac.ecommerce.category.entity.Category;
import com.sheilac.ecommerce.category.repository.CategoryRepository;
import com.sheilac.ecommerce.product.dto.*;
import com.sheilac.ecommerce.product.entity.Product;
import com.sheilac.ecommerce.product.mapper.ProductMapper;
import com.sheilac.ecommerce.product.repository.ProductImageRepository;
import com.sheilac.ecommerce.product.repository.ProductRepository;
import com.sheilac.ecommerce.product.specification.ProductSpecification;
import com.sheilac.ecommerce.utils.config.PaginationConfig;
import com.sheilac.ecommerce.utils.enums.ErrorCode;
import com.sheilac.ecommerce.utils.exceptions.ApiException;
import com.sheilac.ecommerce.utils.response.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductMapper productMapper;
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final PaginationConfig paginationConfig;
    private static final Set<String> SORT_FIELDS =
            Set.of("name", "price", "sku", "stockQuantity", "createdAt", "updatedAt");

    @Override
    public ProductResponse createProduct(ProductRequest request) {

        Brand brand = brandRepository.findById(request.getBrandId())
                .orElseThrow(() -> new ApiException(
                        "Brand with ID " + request.getBrandId() + " not found",
                        ErrorCode.NOT_FOUND
                ));

        if (!Boolean.TRUE.equals(brand.getActive())) {
            throw new ApiException(
                    "Brand with ID " + request.getBrandId() + " is inactive",
                    ErrorCode.BAD_REQUEST
            );
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ApiException(
                        "Category with ID " + request.getCategoryId() + " not found",
                        ErrorCode.NOT_FOUND
                ));

        if (!Boolean.TRUE.equals(category.getActive())) {
            throw new ApiException(
                    "Category with ID " + request.getCategoryId() + " is inactive",
                    ErrorCode.BAD_REQUEST
            );
        }

        Product product = productMapper.toEntity(request);

        product.setBrand(brand);
        product.setCategory(category);
        product.setSku(generateSku(brand));

        Product saved = productRepository.save(product);

        return productMapper.toResponse(saved);
    }

    private String generateSku(Brand brand) {
        Long sequence = productRepository.getNextSkuNumber();
        return String.format("%s-%06d", brand.getCode(), sequence);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ProductResponse> getAllProducts(
            ProductFilterRequest filter,
            int page,
            Integer size,
            String sortBy,
            String sortDirection
    ) {

        if (filter == null) {
            filter = new ProductFilterRequest();
        }

        List<Specification<Product>> specifications = new ArrayList<>();

        if (filter.getSearch() != null && !filter.getSearch().isBlank()) {
            specifications.add(ProductSpecification.search(filter.getSearch()));
        }

        if (filter.getSku() != null && !filter.getSku().isBlank()) {
            specifications.add(ProductSpecification.hasSku(filter.getSku()));
        }

        if (filter.getBrandId() != null) {
            specifications.add(ProductSpecification.hasBrandId(filter.getBrandId()));
        }

        if (filter.getCategoryId() != null) {
            specifications.add(ProductSpecification.hasCategoryId(filter.getCategoryId()));
        }

        if (filter.getMinPrice() != null) {
            specifications.add(
                    ProductSpecification.priceGreaterThanOrEqualTo(filter.getMinPrice())
            );
        }

        if (filter.getMaxPrice() != null) {
            specifications.add(
                    ProductSpecification.priceLessThanOrEqualTo(filter.getMaxPrice())
            );
        }

        if (filter.getMinPrice() != null
                && filter.getMaxPrice() != null
                && filter.getMinPrice().compareTo(filter.getMaxPrice()) > 0) {

            throw new ApiException(
                    "Minimum price cannot be greater than maximum price.",
                    ErrorCode.BAD_REQUEST
            );
        }

        String status = filter.getStatus();

        if (status == null || status.isBlank()) {
            status = "active";
        }

        switch (status.trim().toLowerCase()) {

            case "active" ->
                    specifications.add(ProductSpecification.hasActiveStatus(true));

            case "inactive" ->
                    specifications.add(ProductSpecification.hasActiveStatus(false));

            case "all" -> {
                // No active-status condition.
            }

            default -> throw new ApiException(
                    "Invalid product status. Use active, inactive or all.",
                    ErrorCode.BAD_REQUEST
            );
        }

        Specification<Product> specification = Specification.allOf(specifications);

        Pageable pageable = paginationConfig.createPageable(
                page,
                size,
                sortBy,
                sortDirection,
                SORT_FIELDS
        );

        Page<Product> productPage = productRepository.findAll(
                specification,
                pageable
        );

        List<Long> productIds = productPage.getContent()
                .stream()
                .map(Product::getId)
                .toList();

        Map<Long, String> primaryUrls = productIds.isEmpty()
                ? Map.of()
                : productImageRepository.findPrimaryImagesByProductIds(productIds)
                  .stream()
                  .collect(Collectors.toMap(
                          image -> image.getProduct().getId(),
                          image -> ProductImageResponse.urlFor(image.getStoragePath()),
                          (first, second) -> first
                  ));

        return PageResponse.from(productPage, product -> {

            ProductResponse response = productMapper.toResponse(product);

            response.setPrimaryImageUrl(primaryUrls.get(product.getId()));

            return response;
        });
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {

        Product product = productRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ApiException(
                        "Product with ID " + id + " not found",
                        ErrorCode.NOT_FOUND
                ));

        return withImages(productMapper.toResponse(product), id);
    }

    @Override
    public ProductResponse updateProduct(
            Long id,
            UpdateProductRequest request
    ) {

        Product product = findProduct(id);

        Brand brand = brandRepository.findById(request.getBrandId())
                .orElseThrow(() -> new ApiException(
                        "Brand with ID " + request.getBrandId() + " not found",
                        ErrorCode.NOT_FOUND
                ));

        if (!Boolean.TRUE.equals(brand.getActive())) {
            throw new ApiException(
                    "Brand with ID " + request.getBrandId() + " is inactive",
                    ErrorCode.BAD_REQUEST
            );
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ApiException(
                        "Category with ID " + request.getCategoryId() + " not found",
                        ErrorCode.NOT_FOUND
                ));

        if (!Boolean.TRUE.equals(category.getActive())) {
            throw new ApiException(
                    "Category with ID " + request.getCategoryId() + " is inactive",
                    ErrorCode.BAD_REQUEST
            );
        }

        productMapper.updateEntity(request, product);

        product.setBrand(brand);
        product.setCategory(category);

        Product updated = productRepository.save(product);

        return withImages(productMapper.toResponse(updated), id);
    }

    @Override
    public void deleteProduct(Long id) {

        Product product = findProduct(id);

        if (!Boolean.TRUE.equals(product.getActive())) {
            throw new ApiException(
                    "Product is already inactive",
                    ErrorCode.BAD_REQUEST
            );
        }

        // Soft delete: retain image records and physical files.
        product.setActive(false);

        productRepository.save(product);
    }

    @Override
    public void activateProduct(Long id) {

        Product product = findProduct(id);

        if (Boolean.TRUE.equals(product.getActive())) {
            throw new ApiException(
                    "Product is already active",
                    ErrorCode.BAD_REQUEST
            );
        }

        product.setActive(true);

        productRepository.save(product);
    }

    private Product findProduct(Long id) {

        return productRepository.findById(id)
                .orElseThrow(() -> new ApiException(
                        "Product with ID " + id + " not found",
                        ErrorCode.NOT_FOUND
                ));
    }

    private ProductResponse withImages(
            ProductResponse response,
            Long productId
    ) {

        List<ProductImageResponse> images =
                productImageRepository.findByProductIdOrderByDisplayOrderAsc(productId)
                        .stream()
                        .map(ProductImageResponse::from)
                        .toList();

        response.setImages(images);

        response.setPrimaryImageUrl(
                images.stream()
                        .filter(image -> Boolean.TRUE.equals(image.getPrimary()))
                        .map(ProductImageResponse::getUrl)
                        .findFirst()
                        .orElse(null)
        );

        return response;
    }
}