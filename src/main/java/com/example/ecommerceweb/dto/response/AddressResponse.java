package com.example.ecommerceweb.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddressResponse {
    private Long id;
    private String addressLine;
    private String city;
    private String district;
    private String postcode;
    private String country;
}
