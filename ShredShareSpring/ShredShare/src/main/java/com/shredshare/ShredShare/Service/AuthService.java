package com.shredshare.ShredShare.Service;

import com.shredshare.ShredShare.Entity.User;
import com.shredshare.ShredShare.Repository.UserRepository;
import com.shredshare.ShredShare.dto.Login.LoginRequest;
import com.shredshare.ShredShare.dto.Login.LoginResponse;
import com.shredshare.ShredShare.dto.Register.RegisterRequest;
import com.shredshare.ShredShare.dto.Register.RegisterResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import com.shredshare.ShredShare.dto.Login.ForgotPasswordRequest;
import com.shredshare.ShredShare.dto.Login.ResetPasswordRequest;
import com.shredshare.ShredShare.dto.Admin.SimpleResponse;

import java.time.LocalDateTime;
import java.util.Random;
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            EmailService emailService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    public LoginResponse login(LoginRequest request) {
        Optional<User> userOptional = userRepository.findByEmail(request.getEmail().trim().toLowerCase());

        if (userOptional.isEmpty()) {
            return new LoginResponse(false, "Потребителят не е намерен", null, null);
        }

        User user = userOptional.get();

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return new LoginResponse(false, "Невалидна парола", null, null);
        }

        return new LoginResponse(
                true,
                "Успешно влизане",
                user.getRole(),
                user.getId()
        );
    }

    public RegisterResponse register(RegisterRequest request) {
        if (request.getFirstName() == null || request.getFirstName().isBlank()) {
            return new RegisterResponse(false, "Името е задължително", null, null);
        }

        if (request.getLastName() == null || request.getLastName().isBlank()) {
            return new RegisterResponse(false, "Фамилията е задължителна", null, null);
        }

        if (request.getAddress() == null || request.getAddress().isBlank()) {
            return new RegisterResponse(false, "Адресът е задължителен", null, null);
        }

        if (request.getEmail() == null || request.getEmail().isBlank()) {
            return new RegisterResponse(false, "Имейлът е задължителен", null, null);
        }

        if (request.getPassword() == null || request.getPassword().isBlank()) {
            return new RegisterResponse(false, "Паролата е задължителна", null, null);
        }

        String normalizedEmail = request.getEmail().trim().toLowerCase();
        if (userRepository.existsByEmail(normalizedEmail)) {
            return new RegisterResponse(false, "Имейлът вече съществува", null, null);
        }

        String role = request.getRole();

        if (role == null || role.isBlank()) {
            role = "customer";
        } else {
            role = role.toLowerCase();
        }

        if (!role.equals("customer") && !role.equals("owner")) {
            return new RegisterResponse(false, "Невалидна роля", null, null);
        }

        User user = new User();
        user.setFirstName(request.getFirstName().trim());
        user.setLastName(request.getLastName().trim());
        user.setAddress(request.getAddress().trim());
        user.setEmail(normalizedEmail);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhone(request.getPhone());
        user.setRole(role);

        User savedUser = userRepository.save(user);

        return new RegisterResponse(
                true,
                "Успешна регистрация",
                savedUser.getRole(),
                savedUser.getId()
        );
    }

    public SimpleResponse forgotPassword(ForgotPasswordRequest request) {
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            return new SimpleResponse(false, "Имейлът е задължителен.");
        }

        String normalizedEmail = request.getEmail().trim().toLowerCase();

        Optional<User> userOptional = userRepository.findByEmail(normalizedEmail);

        if (userOptional.isEmpty()) {
            return new SimpleResponse(false, "Няма потребител с такъв имейл.");
        }

        User user = userOptional.get();

        String code = String.format("%06d", new java.util.Random().nextInt(1000000));

        user.setResetCode(code);
        user.setResetCodeExpiry(java.time.LocalDateTime.now().plusMinutes(10));
        userRepository.save(user);

        try {
            emailService.sendResetCodeEmail(normalizedEmail, code);
            return new SimpleResponse(true, "Изпратихме код за възстановяване на имейла.");
        } catch (Exception e) {
            return new SimpleResponse(false, "Неуспешно изпращане на имейл.");
        }
    }

    public SimpleResponse resetPassword(ResetPasswordRequest request) {
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            return new SimpleResponse(false, "Имейлът е задължителен.");
        }

        if (request.getCode() == null || request.getCode().isBlank()) {
            return new SimpleResponse(false, "Кодът е задължителен.");
        }

        if (request.getNewPassword() == null || request.getNewPassword().isBlank()) {
            return new SimpleResponse(false, "Новата парола е задължителна.");
        }

        String normalizedEmail = request.getEmail().trim().toLowerCase();

        Optional<User> userOptional = userRepository.findByEmail(normalizedEmail);

        if (userOptional.isEmpty()) {
            return new SimpleResponse(false, "Потребителят не е намерен.");
        }

        User user = userOptional.get();

        if (user.getResetCode() == null || user.getResetCodeExpiry() == null) {
            return new SimpleResponse(false, "Няма активен код за възстановяване.");
        }

        if (LocalDateTime.now().isAfter(user.getResetCodeExpiry())) {
            return new SimpleResponse(false, "Кодът е изтекъл.");
        }

        if (!user.getResetCode().equals(request.getCode().trim())) {
            return new SimpleResponse(false, "Невалиден код.");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setResetCode(null);
        user.setResetCodeExpiry(null);

        userRepository.save(user);

        return new SimpleResponse(true, "Паролата беше сменена успешно.");
    }
}