package com.sheilac.ecommerce.brand.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "brands",
        schema = "product",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_brand_name",
                        columnNames = "name"
                ),
                @UniqueConstraint(
                        name = "uk_brand_code",
                        columnNames = "code"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Brand {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 5, updatable = false)
    private String code;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    @Version
    private Long version;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {

        if (active == null) {
            active = true;
        }

        if (name != null) {
            name = name.trim();
        }

        if (code != null) {
            code = code.trim().toUpperCase();
        }

        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    public void onUpdate() {

        if (name != null) {
            name = name.trim();
        }

        updatedAt = LocalDateTime.now();
    }
}
