package com.sheilac.ecommerce.product.repository;

import com.sheilac.ecommerce.product.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {

    List<ProductImage> findByProductIdOrderByDisplayOrderAsc(Long productId);

    Optional<ProductImage> findByIdAndProductId(Long imageId, Long productId);

    long countByProductId(Long productId);

    @Query("""
            SELECT MAX(i.displayOrder)
            FROM ProductImage i
            WHERE i.product.id = :productId
            """)
    Integer findMaxDisplayOrderByProductId(@Param("productId") Long productId);

    @Query("""
            SELECT i
            FROM ProductImage i
            WHERE i.product.id IN :productIds
            AND i.primaryImage = true
            """)
    List<ProductImage> findPrimaryImagesByProductIds(
            @Param("productIds") Collection<Long> productIds
    );
}
