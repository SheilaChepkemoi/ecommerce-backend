package com.sheilac.ecommerce.brand.service;

import com.sheilac.ecommerce.brand.dto.BrandRequest;
import com.sheilac.ecommerce.brand.dto.BrandResponse;
import com.sheilac.ecommerce.brand.entity.Brand;
import com.sheilac.ecommerce.brand.mapper.BrandMapper;
import com.sheilac.ecommerce.brand.repository.BrandRepository;
import com.sheilac.ecommerce.product.repository.ProductRepository;
import com.sheilac.ecommerce.utils.config.PaginationConfig;
import com.sheilac.ecommerce.utils.enums.ErrorCode;
import com.sheilac.ecommerce.utils.exceptions.ApiException;
import com.sheilac.ecommerce.utils.response.PageResponse;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;

    private final BrandMapper brandMapper;

    private final ProductRepository productRepository;

    private final PaginationConfig paginationConfig;

    private static final Set<String> SORT_FIELDS =
            Set.of("name", "code", "createdAt", "updatedAt");

    // =========================================================
    // CREATE
    // =========================================================

    @Override

    public BrandResponse createBrand(BrandRequest request) {

        String brandName = request.getName().trim();

        Brand existingBrand = brandRepository
                .findByNameIgnoreCase(brandName)
                .orElse(null);

        /*
         * Brand already exists.
         */
        if (existingBrand != null) {

            /*
             * Brand exists but is inactive.
             * Do NOT activate automatically.
             */
            if (!existingBrand.getActive()) {

                throw new ApiException(
                        "Brand with name "
                                + brandName
                                + " already exists but is inactive. ",
                        ErrorCode.CONFLICT
                );
            }

            /*
             * Brand exists and is active.
             */
            throw new ApiException(
                    "Brand with name "
                            + brandName
                            + " already exists",
                    ErrorCode.CONFLICT
            );
        }

        String code;

        if (request.getCode() != null
                && !request.getCode().isBlank()) {

            code = request.getCode()
                    .trim()
                    .toUpperCase();

            if (brandRepository.existsByCodeIgnoreCase(code)) {

                throw new ApiException(
                        "Brand with code "
                                + code
                                + " already exists",
                        ErrorCode.CONFLICT
                );
            }

        } else {

            code = generateBrandCode(brandName);
        }

        Brand brand = brandMapper.toEntity(request);

        brand.setName(brandName);
        brand.setCode(code);
        brand.setActive(true);

        Brand savedBrand =
                brandRepository.save(brand);

        return brandMapper.toResponse(savedBrand);
    }

    // =========================================================
    // GET ONE
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public BrandResponse getBrand(
            Long id
    ) {

        Brand brand = findBrand(id);

        return brandMapper.toResponse(
                brand
        );
    }
    
    // =========================================================
// GET ALL
// =========================================================

    @Override
    @Transactional(readOnly = true)
    public PageResponse<BrandResponse> getAllBrands(
            String name,
            String code,
            Boolean active,
            int page,
            Integer size,
            String sortBy,
            String sortDirection
    ) {

        Pageable pageable =
                paginationConfig.createPageable(
                        page,
                        size,
                        sortBy,
                        sortDirection,
                        SORT_FIELDS
                );

        // Normalize search parameters
        String nameFilter = (name == null || name.isBlank())
                ? null : name.trim();

        String codeFilter = (code == null || code.isBlank())
                ? null : code.trim();

        // Build search conditions dynamically
        Specification<Brand> specification = (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (nameFilter != null) {
                predicates.add(
                        cb.like(
                                cb.lower(root.get("name")),
                                "%" + nameFilter.toLowerCase() + "%"
                        )
                );
            }

            if (codeFilter != null) {
                predicates.add(
                        cb.like(
                                cb.lower(root.get("code")),
                                "%" + codeFilter.toLowerCase() + "%"
                        )
                );
            }

            if (active != null) {
                predicates.add(
                        cb.equal(root.get("active"), active)
                );
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<Brand> brandPage = brandRepository.findAll(
                specification,
                pageable
        );

        return PageResponse.from(
                brandPage,
                brandMapper::toResponse
        );
    }
    // =========================================================
    // UPDATE
    // =========================================================

    @Override
    public BrandResponse updateBrand(
            Long id,
            BrandRequest request
    ) {

        Brand brand = findBrand(id);

        String newName =
                request.getName().trim();

        // Check if another brand already has this name
        if (!brand.getName().equalsIgnoreCase(newName)
                && brandRepository.existsByNameIgnoreCase(
                newName
        )) {

            throw new ApiException(
                    "Brand with name "
                            + newName
                            + " already exists",
                    ErrorCode.CONFLICT
            );
        }

        /*
         * We intentionally do NOT update the code.
         *
         * Example:
         *
         * Nike → NIK
         *
         * Even if the admin changes the name,
         * the code remains NIK.
         */
        brand.setName(newName);

        Brand updatedBrand =
                brandRepository.save(brand);

        return brandMapper.toResponse(
                updatedBrand
        );
    }

    // =========================================================
    // DEACTIVATE
    // =========================================================

    @Override
    public void deactivateBrand(
            Long id
    ) {

        Brand brand = findBrand(id);

        if (!brand.getActive()) {

            throw new ApiException(
                    "Brand is already inactive",
                    ErrorCode.BAD_REQUEST
            );
        }
        if (productRepository.existsByBrandIdAndActiveTrue(id)) {
            throw new ApiException(
                    "Brand cannot be deleted because it is associated with active products",
                    ErrorCode.BAD_REQUEST
            );
        }

        brand.setActive(false);

        brandRepository.save(brand);
    }

    // =========================================================
    // FIND BRAND
    // =========================================================

    private Brand findBrand(
            Long id
    ) {

        return brandRepository
                .findById(id)
                .orElseThrow(() ->
                        new ApiException(
                                "Brand with ID "
                                        + id
                                        + " not found",
                                ErrorCode.NOT_FOUND
                        )
                );
    }

    // =========================================================
    // GENERATE BRAND CODE
    // =========================================================

    private String generateBrandCode(
            String brandName
    ) {

        String cleaned = brandName
                .replaceAll(
                        "[^a-zA-Z0-9]",
                        ""
                )
                .toUpperCase();

        if (cleaned.length() < 2) {

            throw new ApiException(
                    "Brand name is too short to generate a code. "
                            + "Please provide a custom code.",
                    ErrorCode.BAD_REQUEST
            );
        }

        /*
         * Start with 3 characters.
         *
         * Nike → NIK
         * Adidas → ADI
         * Samsung → SAM
         */

        int maxLength =
                Math.min(cleaned.length(), 5);

        for (
                int length = 3;
                length <= maxLength;
                length++
        ) {

            String code =
                    cleaned.substring(
                            0,
                            length
                    );

            if (!brandRepository
                    .existsByCodeIgnoreCase(code)) {

                return code;
            }
        }

        /*
         * If all automatically generated codes
         * are already taken, ask the admin to
         * provide a custom code.
         */

        throw new ApiException(
                "Unable to generate a unique brand code. "
                        + "Please provide a custom code.",
                ErrorCode.CONFLICT
        );
    }

    @Override
    public void activateBrand(Long id) {

        Brand brand = findBrand(id);

        if (brand.getActive()) {
            throw new ApiException(
                    "Brand is already active",
                    ErrorCode.BAD_REQUEST
            );
        }

        brand.setActive(true);

        brandRepository.save(brand);
    }
}