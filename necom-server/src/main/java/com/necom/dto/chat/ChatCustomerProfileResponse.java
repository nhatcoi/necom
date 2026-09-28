package com.necom.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatCustomerProfileResponse {
    private Long id;
    private String username;
    private String fullname;
    private String email;
    private String phone;
    private Instant createdAt;
    private int rewardScore;
    private long totalOrders;
    private List<OrderSummary> recentOrders;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OrderSummary {
        private Long id;
        private String code;
        private Integer status;
        private String statusLabel;
        private BigDecimal totalPay;
        private Instant createdAt;
    }
}
