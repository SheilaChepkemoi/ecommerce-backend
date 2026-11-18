package com.sheilac.ecommerce.product.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "product_images", schema = "product",
        indexes = @Index(name = "idx_product_images_product_id", columnList = "product_id"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Original client filename, for display only. Never used on disk. */
    @Column(nullable = false)
    private String fileName;

    /** Relative path, e.g. products/42/uuid.jpg */
    @Column(nullable = false, unique = true)
    private String storagePath;

    @Column(nullable = false)
    private String contentType;

    private Long sizeBytes;

    @Column(nullable = false)
    @Builder.Default
    private Integer displayOrder = 0;

    @Column(nullable = false)
    @Builder.Default
    private Boolean primaryImage = false;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
    }
}