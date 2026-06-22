package com.roadready.dto;

import java.util.List;

public record PaginatedResponse<T>(
        List<T> content,
        int pageNo,
        int pageSize,
        long totalElements,
        int totalPages,
        boolean last
) {
}
