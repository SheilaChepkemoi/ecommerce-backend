package com.sheilac.ecommerce.product.controller;

import com.sheilac.ecommerce.product.dto.ProductImageResponse;
import com.sheilac.ecommerce.product.service.ProductImageService;
import com.sheilac.ecommerce.utils.enums.ResponseType;
import com.sheilac.ecommerce.utils.response.CustomResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/products/{productId}/images")
@RequiredArgsConstructor
public class ProductImageController {

    private final ProductImageService imageService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CustomResponse<ProductImageResponse>> upload(
            @PathVariable Long productId,
            @RequestParam("file") MultipartFile file
    ) {
        ProductImageResponse image = imageService.addImage(productId, file);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                CustomResponse.<ProductImageResponse>builder()
                        .responseType(ResponseType.SUCCESS)
                        .generatedId(image.getId())
                        .payload(image)
                        .message("Image uploaded successfully")
                        .messageTracker(System.currentTimeMillis())
                        .build());
    }

    @GetMapping
    public ResponseEntity<CustomResponse<List<ProductImageResponse>>> getImages(
            @PathVariable Long productId
    ) {
        return ResponseEntity.ok(
                CustomResponse.<List<ProductImageResponse>>builder()
                        .responseType(ResponseType.SUCCESS)
                        .payload(imageService.getImages(productId))
                        .message("Images retrieved successfully")
                        .messageTracker(System.currentTimeMillis())
                        .build());
    }

    @PatchMapping("/{imageId}/primary")
    public ResponseEntity<CustomResponse<Void>> setPrimary(
            @PathVariable Long productId,
            @PathVariable Long imageId
    ) {
        imageService.setPrimary(productId, imageId);

        return ResponseEntity.ok(
                CustomResponse.<Void>builder()
                        .responseType(ResponseType.SUCCESS)
                        .message("Primary image updated successfully")
                        .messageTracker(System.currentTimeMillis())
                        .build());
    }

    @DeleteMapping("/{imageId}")
    public ResponseEntity<CustomResponse<Void>> delete(
            @PathVariable Long productId,
            @PathVariable Long imageId
    ) {
        imageService.deleteImage(productId, imageId);

        return ResponseEntity.ok(
                CustomResponse.<Void>builder()
                        .responseType(ResponseType.SUCCESS)
                        .message("Image deleted successfully")
                        .messageTracker(System.currentTimeMillis())
                        .build());
    }
}
