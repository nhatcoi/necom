package com.example.ecommerceweb.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class OrderFilter {
    private String search;
    private String status;
    private String paymentStatus;
    private LocalDate fromDate;
    private LocalDate toDate;
    private String sortBy;
}