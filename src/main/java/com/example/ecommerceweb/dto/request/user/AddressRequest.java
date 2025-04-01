package com.example.ecommerceweb.dto.request.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddressRequest {
    private Long id;
    private String addressLine;
    private String city;
    private String district;
    private String postcode;
    private String country;
    private Boolean isMain;
}
