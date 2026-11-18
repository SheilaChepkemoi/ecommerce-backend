package com.sheilac.ecommerce.product.mapper;

import com.sheilac.ecommerce.product.dto.ProductRequest;
import com.sheilac.ecommerce.product.dto.ProductResponse;
import com.sheilac.ecommerce.product.dto.UpdateProductRequest;
import com.sheilac.ecommerce.product.entity.Product;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "brand", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "sku", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "images", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Product toEntity(ProductRequest request);

    @Mapping(target = "brandId", source = "brand.id")
    @Mapping(target = "brandName", source = "brand.name")
    @Mapping(target = "brandCode", source = "brand.code")
    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "categoryName", source = "category.name")
    // Filled in by the service. Ignoring stops MapStruct from touching the lazy collection.
    @Mapping(target = "images", ignore = true)
    @Mapping(target = "primaryImageUrl", ignore = true)
    ProductResponse toResponse(Product product);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "sku", ignore = true)
    @Mapping(target = "brand", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "images", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(
            UpdateProductRequest request,
            @MappingTarget Product product
    );
}