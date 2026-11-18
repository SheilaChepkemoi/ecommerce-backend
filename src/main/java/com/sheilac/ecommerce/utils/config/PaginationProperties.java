package com.sheilac.ecommerce.utils.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "pagination")

    public class PaginationProperties {

        private int defaultPageSize = 20;

        private int defaultPage = 0;

        private String defaultSortBy = "createdAt";

        private String defaultSortDirection = "desc";

    private List<Integer> allowedPageSizes = List.of(20, 50, 100);

}
