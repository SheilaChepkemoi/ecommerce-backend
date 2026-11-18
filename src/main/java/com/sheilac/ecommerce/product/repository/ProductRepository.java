package com.sheilac.ecommerce.product.repository;

import com.sheilac.ecommerce.product.entity.Product;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>,
        JpaSpecificationExecutor<Product> {

    boolean existsBySku(String sku);

    @Query(
            value = "SELECT nextval('product.sku_sequence')",
            nativeQuery = true
    )
    Long getNextSkuNumber();

    Optional<Product> findByIdAndActiveTrue(Long id);

    boolean existsByBrandIdAndActiveTrue(Long brandId);

    boolean existsByCategoryIdAndActiveTrue(Long categoryId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Product p WHERE p.id = :productId")
    Optional<Product> findByIdForUpdate(@Param("productId") Long productId);
}
