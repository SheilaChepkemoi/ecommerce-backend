package com.sheilac.ecommerce.brand.repository;

import com.sheilac.ecommerce.brand.entity.Brand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BrandRepository extends JpaRepository <Brand, Long>, JpaSpecificationExecutor<Brand>{

    boolean existsByNameIgnoreCase(String name);

    boolean existsByCodeIgnoreCase(String code);

    Optional<Brand> findByNameIgnoreCase(String name);

    Optional<Brand> findByCodeIgnoreCase(String code);
    

}
