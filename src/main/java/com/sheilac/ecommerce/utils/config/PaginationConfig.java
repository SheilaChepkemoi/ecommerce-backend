package com.sheilac.ecommerce.utils.config;

import com.sheilac.ecommerce.utils.enums.ErrorCode;
import com.sheilac.ecommerce.utils.exceptions.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class PaginationConfig {

    private static final String TIE_BREAKER_FIELD = "id";

    private final PaginationProperties paginationProperties;

    /**
     * Builds a Pageable from request parameters.
     *
     * @param page              zero-based page number (0 = first page)
     * @param size              page size; null means use the default
     * @param sortBy            entity field to sort by; null/blank means use the default
     * @param sortDirection     "asc" or "desc"; null/blank means use the default
     * @param allowedSortFields the entity fields the caller is allowed to sort by.
     *                          Anything else is rejected with 400 instead of
     *                          crashing in the database layer with a 500.
     */
    public Pageable createPageable(
            int page,
            Integer size,
            String sortBy,
            String sortDirection,
            Set<String> allowedSortFields
    ) {

        // 1. Page number
        if (page < 0) {
            throw new ApiException(
                    "Page number cannot be negative",
                    ErrorCode.BAD_REQUEST
            );
        }

        // 2. Sort field (default + whitelist)
        String finalSortBy =
                sortBy == null || sortBy.isBlank()
                        ? paginationProperties.getDefaultSortBy()
                        : sortBy.trim();

        if (!allowedSortFields.contains(finalSortBy)) {
            throw new ApiException(
                    "Cannot sort by '" + finalSortBy
                            + "'. Allowed fields: " + allowedSortFields,
                    ErrorCode.BAD_REQUEST
            );
        }

        // 3. Sort direction
        String finalSortDirection =
                sortDirection == null || sortDirection.isBlank()
                        ? paginationProperties.getDefaultSortDirection()
                        : sortDirection.trim();

        Sort.Direction direction = Sort.Direction
                .fromOptionalString(finalSortDirection)
                .orElseThrow(() -> new ApiException(
                        "Sort direction must be 'asc' or 'desc'",
                        ErrorCode.BAD_REQUEST
                ));

        // 4. Page size
        int finalPageSize =
                size == null
                        ? paginationProperties.getDefaultPageSize()
                        : size;

        if (!paginationProperties.getAllowedPageSizes().contains(finalPageSize)) {
            throw new ApiException(
                    "Page size must be one of: "
                            + paginationProperties.getAllowedPageSizes(),
                    ErrorCode.BAD_REQUEST
            );
        }

        Sort sort = Sort.by(direction, finalSortBy);

        if (!TIE_BREAKER_FIELD.equals(finalSortBy)) {
            sort = sort.and(Sort.by(direction, TIE_BREAKER_FIELD));
        }

        return PageRequest.of(
                page,
                finalPageSize,
                sort
        );
    }
}