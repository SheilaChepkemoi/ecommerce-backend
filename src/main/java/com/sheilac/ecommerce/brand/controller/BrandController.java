package com.sheilac.ecommerce.brand.controller;

import com.sheilac.ecommerce.brand.dto.BrandRequest;
import com.sheilac.ecommerce.brand.dto.BrandResponse;
import com.sheilac.ecommerce.brand.service.BrandService;

import com.sheilac.ecommerce.utils.enums.ResponseType;
import com.sheilac.ecommerce.utils.response.CustomResponse;
import com.sheilac.ecommerce.utils.response.PageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/brands")
@RequiredArgsConstructor
public class BrandController {

    private final BrandService brandService;

    @PostMapping
    public ResponseEntity<CustomResponse<BrandResponse>>createBrand(
            @Valid @RequestBody BrandRequest request
    ) {

        BrandResponse brand = brandService.createBrand(request);

        CustomResponse<BrandResponse> response =
                CustomResponse
                        .<BrandResponse>builder()
                        .responseType(ResponseType.SUCCESS)
                        .generatedId(brand.getId())
                        .payload(brand)
                        .message("Brand created successfully")
                        .messageTracker(System.currentTimeMillis())
                        .build();

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


    @GetMapping("/{id}")
    public ResponseEntity<CustomResponse<BrandResponse>> getBrand(
            @PathVariable Long id
    ) {

        BrandResponse brand = brandService.getBrand(id);

        CustomResponse<BrandResponse> response =
                CustomResponse
                        .<BrandResponse>builder()
                        .responseType(ResponseType.SUCCESS)
                        .payload(brand)
                        .message("Brand retrieved successfully")
                        .build();

        return ResponseEntity.ok(response);
    }


    @GetMapping
    public ResponseEntity<CustomResponse<PageResponse<BrandResponse>>> getAllBrands(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortDirection,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String code,
            @RequestParam(required = false) Boolean active
    ) {

        PageResponse<BrandResponse> brands =
                brandService.getAllBrands(
                        name,
                        code,
                        active,
                        page,
                        size,
                        sortBy,
                        sortDirection
                );

        CustomResponse<PageResponse<BrandResponse>> response =
                CustomResponse
                        .<PageResponse<BrandResponse>>builder()
                        .responseType(ResponseType.SUCCESS)
                        .payload(brands)
                        .message("Brands retrieved successfully")
                        .build();

        return ResponseEntity.ok(response);
    }


    @PutMapping("/{id}")
    public ResponseEntity<CustomResponse<BrandResponse>> updateBrand(
            @PathVariable Long id,
            @Valid @RequestBody BrandRequest request
    ) {

        BrandResponse brand = brandService.updateBrand(id, request);

        CustomResponse<BrandResponse> response =
                CustomResponse
                        .<BrandResponse>builder()
                        .responseType(ResponseType.SUCCESS)
                        .payload(brand)
                        .message("Brand successfully edited")
                        .build();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }



    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<CustomResponse<Void>> deactivateBrand(
            @PathVariable Long id
    ) {

     brandService.deactivateBrand(id);

        CustomResponse<Void> response =
                CustomResponse
                        .<Void>builder()
                        .responseType(ResponseType.SUCCESS)
                        .generatedId(id)
                        .message("Brand deactivated successfully")
                        .messageTracker(System.currentTimeMillis())
                        .build();

        return ResponseEntity.ok(response);

    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<CustomResponse<Void>> activateBrand(
            @PathVariable Long id
    ) {
        brandService.activateBrand(id);

        CustomResponse<Void> response =
                CustomResponse.<Void>builder()
                        .responseType(ResponseType.SUCCESS)
                        .generatedId(id)
                        .message("Brand activated successfully")
                        .messageTracker(System.currentTimeMillis())
                        .build();

        return ResponseEntity.ok(response);
    }
}