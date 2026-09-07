package com.sheilac.ecommerce.product.specification;

import com.sheilac.ecommerce.product.entity.Product;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class ProductSpecification {

    private ProductSpecification() {
        // Utility class - prevent creating objects
    }

    /**
     * Filter products by brand.
     */

    public static Specification<Product> hasBrand(String brand) {

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        criteriaBuilder.lower(root.get("brand")),
                        brand.toLowerCase()
                );
    }

    /**
     * Search products by name, description, brand or SKU.
     */
    public static Specification<Product> search(String search) {

        return (root, query, criteriaBuilder) -> {

            String searchPattern =
                    "%" + search.toLowerCase() + "%";

            return criteriaBuilder.or(

                    criteriaBuilder.like(
                            criteriaBuilder.lower(
                                    root.get("name")
                            ),
                            searchPattern
                    ),

                    criteriaBuilder.like(
                            criteriaBuilder.lower(
                                    root.get("description")
                            ),
                            searchPattern
                    ),

                    criteriaBuilder.like(
                            criteriaBuilder.lower(
                                    root.get("brand")
                            ),
                            searchPattern
                    ),

                    criteriaBuilder.like(
                            criteriaBuilder.lower(
                                    root.get("sku")
                            ),
                            searchPattern
                    )
            );
        };
    }

    /**
     * Filter products whose price is greater than
     * or equal to the minimum price.
     */
    public static Specification<Product> priceGreaterThanOrEqualTo(
            BigDecimal minPrice
    ) {

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(
                        root.get("price"),
                        minPrice
                );
    }


    /**
     * Filter products whose price is less than
     * or equal to the maximum price.
     */
    public static Specification<Product> priceLessThanOrEqualTo(
            BigDecimal maxPrice
    ) {

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.lessThanOrEqualTo(
                        root.get("price"),
                        maxPrice
                );
    }

    /**
     * Filter products by active status.
     */
    public static Specification<Product> hasActiveStatus(
            Boolean active
    ) {

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        root.get("active"),
                        active
                );
    }

}
