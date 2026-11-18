package com.sheilac.ecommerce.category.repository;

import com.sheilac.ecommerce.category.entity.Category;
import com.sheilac.ecommerce.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    boolean existsByNameIgnoreCase(String name);

    Optional<Category> findByIdAndActiveTrue(Long id);

    List<Category> findAllByActiveTrue();

    List<Category> findAllByNameContainingIgnoreCaseAndActiveTrue(
            String name
    );

    Page<Category> findAllByNameContainingIgnoreCaseAndActiveTrue(
            String name,
            Pageable pageable
    );

    Page<Category> findAllByActiveTrue(
            Pageable pageable
    );
}
