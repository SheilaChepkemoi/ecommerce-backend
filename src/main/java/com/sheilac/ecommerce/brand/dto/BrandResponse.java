package com.sheilac.ecommerce.brand.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BrandResponse {

    private Long id;
    private String name;
    private String code;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
