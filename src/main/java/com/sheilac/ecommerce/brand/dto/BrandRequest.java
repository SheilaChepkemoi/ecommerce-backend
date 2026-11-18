package com.sheilac.ecommerce.brand.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class BrandRequest {

    @NotBlank(message = "Brand name is required")
    @Size(
            max = 100,
            message = "Brand name cannot exceed 100 characters"
    )
    private String name;

    @Size(
            min = 2,
            max = 5,
            message = "Brand code must be between 2 and 5 characters"
    )
    @Pattern(
            regexp = "^[a-zA-Z0-9]*$",
            message = "Brand code can only contain letters and numbers"
    )
    private String code;
}
