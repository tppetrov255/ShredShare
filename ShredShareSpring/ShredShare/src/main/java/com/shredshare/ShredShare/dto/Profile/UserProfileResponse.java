package com.shredshare.ShredShare.dto.Profile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileResponse {
    private Integer userId;
    private String firstName;
    private String lastName;
    private String address;
    private String email;
    private String phone;
    private String role;
}
