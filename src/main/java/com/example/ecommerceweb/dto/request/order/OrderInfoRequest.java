package com.example.ecommerceweb.dto.request.order;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderInfoRequest {
    private String fullName;
    private String email;
    private String phoneNumber;

    private Long userAddressId;
    private Boolean isUserAddress;
    private String country;
    private String district;
    private String city;
    private String addressLine;
    private String postcode;
    private Float totalPrice;
    private String note;
}
