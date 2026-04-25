package com.shredshare.ShredShare.dto.Register;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterResponse {
    private boolean success;
    private String message;
    private String role;
    private Integer userId;
}