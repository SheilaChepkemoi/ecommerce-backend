package com.sheilac.ecommerce.product.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductFilterRequest {

        private String search;

        private String sku;

        private BigDecimal minPrice;

        private BigDecimal maxPrice;

        private Long categoryId;

        private Long brandId;

        private String status = "active";
}
