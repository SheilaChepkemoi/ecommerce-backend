package com.sheilac.ecommerce.product.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter


public class ProductResponse {

    private Long id;

    private String name;

    private String description;

    private BigDecimal price;

    private String sku;

    private String brand;

    private Integer stockQuantity;

    private Boolean active;
    
    private Long categoryId;

    private String categoryName;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
