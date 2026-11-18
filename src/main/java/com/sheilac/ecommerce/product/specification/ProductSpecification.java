
package com.sheilac.ecommerce.product.specification;

import com.sheilac.ecommerce.product.entity.Product;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class ProductSpecification {

    // General search across product name, description, brand, category and SKU
    public static Specification<Product> search(String search) {
        return (root, query, criteriaBuilder) -> {

            String searchPattern = "%" + search.trim().toLowerCase() + "%";

            return criteriaBuilder.or(
                    criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("name")),
                            searchPattern
                    ),
                    criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("description")),
                            searchPattern
                    ),
                    criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("brand").get("name")),
                            searchPattern
                    ),
                    criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("category").get("name")),
                            searchPattern
                    ),
                    criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("sku")),
                            searchPattern
                    )
            );
        };
    }

    // Search by SKU only
    public static Specification<Product> hasSku(String sku) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("sku")),
                        "%" + sku.trim().toLowerCase() + "%"
                );
    }

    // Filter by brand
    public static Specification<Product> hasBrandId(Long brandId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        root.get("brand").get("id"),
                        brandId
                );
    }

    // Filter by category
    public static Specification<Product> hasCategoryId(Long categoryId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        root.get("category").get("id"),
                        categoryId
                );
    }

    // Minimum price
    public static Specification<Product> priceGreaterThanOrEqualTo(
            BigDecimal minPrice
    ) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(
                        root.get("price"),
                        minPrice
                );
    }

    // Maximum price
    public static Specification<Product> priceLessThanOrEqualTo(
            BigDecimal maxPrice
    ) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.lessThanOrEqualTo(
                        root.get("price"),
                        maxPrice
                );
    }

    // Filter by active/inactive status
    public static Specification<Product> hasActiveStatus(Boolean active) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        root.get("active"),
                        active
                );
    }
}
