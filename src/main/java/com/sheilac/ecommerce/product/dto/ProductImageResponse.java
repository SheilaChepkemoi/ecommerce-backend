package com.sheilac.ecommerce.product.dto;

import com.sheilac.ecommerce.product.entity.ProductImage;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProductImageResponse {

    private Long id;
    private String url;
    private Boolean primary;
    private Integer displayOrder;

    public static String urlFor(String storagePath) {
        return "/images/" + storagePath;
    }

    public static ProductImageResponse from(ProductImage image) {
        return ProductImageResponse.builder()
                .id(image.getId())
                .url(urlFor(image.getStoragePath()))
                .primary(image.getPrimaryImage())
                .displayOrder(image.getDisplayOrder())
                .build();
    }
}
