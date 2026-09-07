package com.sheilac.ecommerce.product.mapper;

import com.sheilac.ecommerce.product.dto.ProductRequest;
import com.sheilac.ecommerce.product.dto.ProductResponse;
import com.sheilac.ecommerce.product.entity.Product;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(
            target = "category",
            ignore = true
    )
    Product toEntity(ProductRequest request);


    @Mapping(
            target = "categoryId",
            source = "category.id"
    )
    @Mapping(
            target = "categoryName",
            source = "category.name"
    )
    ProductResponse toResponse(Product product);


    @Mapping(
            target = "category",
            ignore = true
    )
    void updateEntity(
            ProductRequest request,
            @MappingTarget Product product
    );
}
