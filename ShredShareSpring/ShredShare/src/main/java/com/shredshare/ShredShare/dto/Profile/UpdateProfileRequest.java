package com.shredshare.ShredShare.dto.Profile;

import lombok.Data;

@Data
public class UpdateProfileRequest {
    private String firstName;
    private String lastName;
    private String address;
    private String email;
    private String phone;
}
