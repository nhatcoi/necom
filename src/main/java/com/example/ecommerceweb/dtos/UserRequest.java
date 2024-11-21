package com.example.ecommerceweb.dtos;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRequest {
    private String phoneNumber;
    private String password;
    private Integer roleId;
}
