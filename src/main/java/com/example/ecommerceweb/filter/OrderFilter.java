package com.example.ecommerceweb.filter;

import lombok.Data;

import java.time.LocalDate;

@Data
public class OrderFilter {
    private String search;
    private String status;
    private String paymentStatus;
    private LocalDate dateFrom;
    private LocalDate dateTo;
    private String SortBy;
}
