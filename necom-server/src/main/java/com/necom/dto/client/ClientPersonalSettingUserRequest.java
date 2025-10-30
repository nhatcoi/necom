package com.necom.dto.client;

import com.necom.dto.address.AddressRequest;
import lombok.Data;

@Data
public class ClientPersonalSettingUserRequest {
    private String username;
    private String fullname;
    private String gender;
    private AddressRequest address;
}
