package com.sheilac.ecommerce.utils.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {

        private List<T> content;
        private int page;
        private int size;
        private long totalElements;
        private int totalPages;

        public static <T, R> PageResponse<R> from(
                Page<T> page,
                Function<T, R> mapper
        ) {
                return PageResponse.<R>builder()
                        .content(
                                page.getContent()
                                        .stream()
                                        .map(mapper)
                                        .toList()
                        )
                        .page(page.getNumber())
                        .size(page.getSize())
                        .totalElements(page.getTotalElements())
                        .totalPages(page.getTotalPages())
                        .build();
        }
}