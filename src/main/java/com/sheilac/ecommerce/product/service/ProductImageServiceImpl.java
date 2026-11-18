package com.sheilac.ecommerce.product.service;

import com.sheilac.ecommerce.product.dto.ProductImageResponse;
import com.sheilac.ecommerce.product.entity.Product;
import com.sheilac.ecommerce.product.entity.ProductImage;
import com.sheilac.ecommerce.product.repository.ProductImageRepository;
import com.sheilac.ecommerce.product.repository.ProductRepository;
import com.sheilac.ecommerce.utils.enums.ErrorCode;
import com.sheilac.ecommerce.utils.exceptions.ApiException;
import com.sheilac.ecommerce.utils.exceptions.FileStorageException;
import com.sheilac.ecommerce.utils.storage.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductImageServiceImpl implements ProductImageService {

    private static final long MAX_FILE_SIZE = 5L * 1024 * 1024;

    private static final int MAX_IMAGES_PER_PRODUCT = 10;

    private static final Map<String, String> ALLOWED_TYPES = Map.of(
            "image/jpeg", "jpg",
            "image/png", "png",
            "image/webp", "webp"
    );

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final FileStorageService fileStorageService;

    @Override
    public ProductImageResponse addImage(
            Long productId,
            MultipartFile file
    ) {

        validateImage(file);

        // Lock the product while checking the image limit and assigning order.
        Product product = findLockedProduct(productId);

        if (!Boolean.TRUE.equals(product.getActive())) {
            throw new ApiException(
                    "Cannot add images to an inactive product.",
                    ErrorCode.BAD_REQUEST
            );
        }

        List<ProductImage> existingImages =
                productImageRepository.findByProductIdOrderByDisplayOrderAsc(productId);

        if (existingImages.size() >= MAX_IMAGES_PER_PRODUCT) {
            throw new ApiException(
                    "A product cannot have more than "
                            + MAX_IMAGES_PER_PRODUCT + " images.",
                    ErrorCode.BAD_REQUEST
            );
        }

        String contentType = file.getContentType().toLowerCase(Locale.ROOT);
        String extension = ALLOWED_TYPES.get(contentType);

        String storagePath = fileStorageService.store(
                file,
                "products/" + productId,
                extension
        );

        // If the database transaction rolls back, remove the uploaded file.
        deleteFileOnRollback(storagePath);

        int displayOrder = existingImages.stream()
                .mapToInt(ProductImage::getDisplayOrder)
                .max()
                .orElse(-1) + 1;

        boolean isFirstImage = existingImages.isEmpty();

        ProductImage image = ProductImage.builder()
                .fileName(getSafeFileName(file.getOriginalFilename()))
                .storagePath(storagePath)
                .contentType(contentType)
                .sizeBytes(file.getSize())
                .displayOrder(displayOrder)
                .primaryImage(isFirstImage)
                .product(product)
                .build();

        ProductImage savedImage = productImageRepository.saveAndFlush(image);

        return ProductImageResponse.from(savedImage);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductImageResponse> getImages(Long productId) {

        if (!productRepository.existsById(productId)) {
            throw new ApiException(
                    "Product with ID " + productId + " not found.",
                    ErrorCode.NOT_FOUND
            );
        }

        return productImageRepository
                .findByProductIdOrderByDisplayOrderAsc(productId)
                .stream()
                .map(ProductImageResponse::from)
                .toList();
    }

    @Override
    public void deleteImage(
            Long productId,
            Long imageId
    ) {

        // Serialize image changes for this product.
        findLockedProduct(productId);

        ProductImage image = productImageRepository
                .findByIdAndProductId(imageId, productId)
                .orElseThrow(() -> new ApiException(
                        "Image with ID " + imageId
                                + " was not found for product " + productId + ".",
                        ErrorCode.NOT_FOUND
                ));

        String storagePath = image.getStoragePath();

        boolean wasPrimary = Boolean.TRUE.equals(image.getPrimaryImage());

        productImageRepository.delete(image);

        // Remove the database record before promoting another primary image.
        productImageRepository.flush();

        if (wasPrimary) {

            List<ProductImage> remainingImages =
                    productImageRepository
                            .findByProductIdOrderByDisplayOrderAsc(productId);

            if (!remainingImages.isEmpty()) {

                ProductImage nextPrimary = remainingImages.get(0);

                nextPrimary.setPrimaryImage(true);

                productImageRepository.saveAndFlush(nextPrimary);
            }
        }

        // Delete the physical file only after the database transaction commits.
        deleteFileAfterCommit(storagePath);
    }

    @Override
    public void setPrimary(
            Long productId,
            Long imageId
    ) {

        findLockedProduct(productId);

        List<ProductImage> images =
                productImageRepository
                        .findByProductIdOrderByDisplayOrderAsc(productId);

        ProductImage selectedImage = images.stream()
                .filter(image -> image.getId().equals(imageId))
                .findFirst()
                .orElseThrow(() -> new ApiException(
                        "Image with ID " + imageId
                                + " was not found for product " + productId + ".",
                        ErrorCode.NOT_FOUND
                ));

        // Clear the existing primary image first.
        images.forEach(image -> image.setPrimaryImage(false));

        productImageRepository.flush();

        // Then assign the selected image as primary.
        selectedImage.setPrimaryImage(true);

        productImageRepository.saveAndFlush(selectedImage);
    }

    private Product findLockedProduct(Long productId) {

        return productRepository.findByIdForUpdate(productId)
                .orElseThrow(() -> new ApiException(
                        "Product with ID " + productId + " not found.",
                        ErrorCode.NOT_FOUND
                ));
    }

    private void validateImage(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new ApiException(
                    "Please select an image to upload.",
                    ErrorCode.BAD_REQUEST
            );
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new ApiException(
                    "Image size cannot exceed 5 MB.",
                    ErrorCode.CONTENT_TOO_LARGE
            );
        }

        String contentType = file.getContentType();

        if (contentType == null || contentType.isBlank()) {
            throw new ApiException(
                    "Unable to determine the image type.",
                    ErrorCode.BAD_REQUEST
            );
        }

        contentType = contentType.toLowerCase(Locale.ROOT);

        if (!ALLOWED_TYPES.containsKey(contentType)) {
            throw new ApiException(
                    "Only JPEG, PNG and WebP images are allowed.",
                    ErrorCode.BAD_REQUEST
            );
        }

        try (InputStream inputStream = file.getInputStream()) {

            byte[] header = inputStream.readNBytes(12);

            if (!hasValidImageSignature(contentType, header)) {
                throw new ApiException(
                        "The uploaded file is not a valid "
                                + contentType + " image.",
                        ErrorCode.BAD_REQUEST
                );
            }

        } catch (IOException exception) {
            throw new FileStorageException(
                    "Unable to read the uploaded image.",
                    exception
            );
        }

        // ImageIO can decode JPEG and PNG using standard Java image readers.
        // WebP is signature-checked above; full WebP decoding requires a
        // WebP-capable ImageIO plugin.
        if (contentType.equals("image/jpeg")
                || contentType.equals("image/png")) {

            try (InputStream inputStream = file.getInputStream()) {

                BufferedImage image = ImageIO.read(inputStream);

                if (image == null) {
                    throw new ApiException(
                            "The uploaded image could not be decoded.",
                            ErrorCode.BAD_REQUEST
                    );
                }

            } catch (IOException exception) {
                throw new FileStorageException(
                        "Unable to validate the uploaded image.",
                        exception
                );
            }
        }
    }

    private boolean hasValidImageSignature(
            String contentType,
            byte[] header
    ) {

        if (contentType.equals("image/jpeg")) {
            return header.length >= 3
                    && (header[0] & 0xFF) == 0xFF
                    && (header[1] & 0xFF) == 0xD8
                    && (header[2] & 0xFF) == 0xFF;
        }

        if (contentType.equals("image/png")) {

            byte[] pngSignature = {
                    (byte) 0x89, 0x50, 0x4E, 0x47,
                    0x0D, 0x0A, 0x1A, 0x0A
            };

            if (header.length < pngSignature.length) {
                return false;
            }

            for (int i = 0; i < pngSignature.length; i++) {
                if (header[i] != pngSignature[i]) {
                    return false;
                }
            }

            return true;
        }

        if (contentType.equals("image/webp")) {

            return header.length >= 12
                    && header[0] == 'R'
                    && header[1] == 'I'
                    && header[2] == 'F'
                    && header[3] == 'F'
                    && header[8] == 'W'
                    && header[9] == 'E'
                    && header[10] == 'B'
                    && header[11] == 'P';
        }

        return false;
    }

    private String getSafeFileName(String originalFileName) {

        if (originalFileName == null || originalFileName.isBlank()) {
            return "image";
        }

        // Remove any path information supplied by the client.
        String safeName = originalFileName.replace('\\', '/');

        int lastSlash = safeName.lastIndexOf('/');

        if (lastSlash >= 0) {
            safeName = safeName.substring(lastSlash + 1);
        }

        safeName = safeName
                .replaceAll("[\\r\\n\\t]", "_")
                .trim();

        if (safeName.isBlank()) {
            return "image";
        }

        // ProductImage.fileName uses the default VARCHAR(255) length.
        return safeName.length() > 255
                ? safeName.substring(0, 255)
                : safeName;
    }

    private void deleteFileOnRollback(String storagePath) {

        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            throw new IllegalStateException(
                    "No active transaction synchronization for image upload."
            );
        }

        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {

                    @Override
                    public void afterCompletion(int status) {

                        if (status != STATUS_COMMITTED) {
                            try {
                                fileStorageService.delete(storagePath);
                            } catch (RuntimeException exception) {
                                log.error(
                                        "Failed to remove uploaded file after transaction rollback: {}",
                                        storagePath,
                                        exception
                                );
                            }
                        }
                    }
                }
        );
    }

    private void deleteFileAfterCommit(String storagePath) {

        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            throw new IllegalStateException(
                    "No active transaction synchronization for image deletion."
            );
        }

        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {

                    @Override
                    public void afterCommit() {

                        try {
                            fileStorageService.delete(storagePath);
                        } catch (RuntimeException exception) {
                            log.error(
                                    "Failed to delete image file after database commit: {}",
                                    storagePath,
                                    exception
                            );
                        }
                    }
                }
        );
    }
}