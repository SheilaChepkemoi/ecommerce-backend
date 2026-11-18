package com.sheilac.ecommerce.brand.service;

import com.sheilac.ecommerce.brand.dto.BrandRequest;
import com.sheilac.ecommerce.brand.dto.BrandResponse;
import com.sheilac.ecommerce.utils.response.PageResponse;

public interface BrandService {

    BrandResponse createBrand(
            BrandRequest request
    );

    BrandResponse getBrand(
            Long id
    );

    PageResponse<BrandResponse> getAllBrands(
            String name,
            String code,
            Boolean active,
            int page,
            Integer size,
            String sortBy,
            String sortDirection
    );

    BrandResponse updateBrand(
            Long id,
            BrandRequest request
    );

    void deactivateBrand(
            Long id
    );

    void activateBrand(
            Long id
    );
}