package com.sheilac.ecommerce.product.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductFilterRequest {

        private String search;

        private String brand;

        private BigDecimal minPrice;

        private BigDecimal maxPrice;

        private Boolean active;

}
