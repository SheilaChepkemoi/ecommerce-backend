package com.sheilac.ecommerce.brand.mapper;

import com.sheilac.ecommerce.brand.dto.BrandRequest;
import com.sheilac.ecommerce.brand.dto.BrandResponse;
import com.sheilac.ecommerce.brand.entity.Brand;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface BrandMapper {

    Brand toEntity(BrandRequest request);

    BrandResponse toResponse(Brand brand);

    void updateEntity(
            BrandRequest request,
            @MappingTarget Brand brand
    );
}