package com.example.ecommerceweb.dto.response_data;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

@Setter
@Getter
public class Pagination {
    private int currentPage;
    private int pageSize;
    private int totalPages;
    private long totalItems;
    private boolean isFirstPage;
    private boolean isLastPage;
    private int numberOfElementsOnPage;
    private boolean hasNextPage;
    private boolean hasPreviousPage;

    public <T> Pagination(Page<T> page) {
        this.currentPage = page.getNumber();
        this.pageSize = page.getSize();
        this.totalPages = page.getTotalPages();
        this.totalItems = page.getTotalElements();
        this.isFirstPage = page.isFirst();
        this.isLastPage = page.isLast();
        this.numberOfElementsOnPage = page.getNumberOfElements();
        this.hasNextPage = page.hasNext();
        this.hasPreviousPage = page.hasPrevious();
    }
}
