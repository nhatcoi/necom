package com.example.ecommerceweb.dto.response_data;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaginatedResponse<T> {
    private List<T> content;
    private int totalPages;
    private long totalElements;
    private int currentPage;
    private int pageSize;
}
