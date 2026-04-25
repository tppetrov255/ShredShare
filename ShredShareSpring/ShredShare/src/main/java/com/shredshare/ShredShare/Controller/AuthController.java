package com.shredshare.ShredShare.Controller;

import com.shredshare.ShredShare.Service.AuthService;
import com.shredshare.ShredShare.dto.Login.LoginRequest;
import com.shredshare.ShredShare.dto.Login.LoginResponse;
import com.shredshare.ShredShare.dto.Register.RegisterRequest;
import com.shredshare.ShredShare.dto.Register.RegisterResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.shredshare.ShredShare.dto.Login.ForgotPasswordRequest;
import com.shredshare.ShredShare.dto.Login.ResetPasswordRequest;
import com.shredshare.ShredShare.dto.Admin.SimpleResponse;
@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<SimpleResponse> forgotPassword(
            @RequestBody ForgotPasswordRequest request
    ) {
        return ResponseEntity.ok(authService.forgotPassword(request));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<SimpleResponse> resetPassword(
            @RequestBody ResetPasswordRequest request
    ) {
        return ResponseEntity.ok(authService.resetPassword(request));
    }
}