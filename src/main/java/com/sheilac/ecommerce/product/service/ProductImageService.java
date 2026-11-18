package com.sheilac.ecommerce.product.service;

import com.sheilac.ecommerce.product.dto.ProductImageResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductImageService {

    ProductImageResponse addImage(Long productId, MultipartFile file);

    List<ProductImageResponse> getImages(Long productId);

    void deleteImage(Long productId, Long imageId);

    void setPrimary(Long productId, Long imageId);
}
