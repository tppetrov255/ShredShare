package com.shredshare.ShredShare.dto.Register;


import lombok.Data;

@Data
public class RegisterRequest {
    private String firstName;
    private String lastName;
    private String address;
    private String email;
    private String password;
    private String phone;
    private String role;
}
