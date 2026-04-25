package com.shredshare.ShredShare.Service;


import com.shredshare.ShredShare.Entity.User;
import com.shredshare.ShredShare.Repository.UserRepository;
import com.shredshare.ShredShare.dto.Profile.UpdateProfileRequest;
import com.shredshare.ShredShare.dto.Profile.UserProfileResponse;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserProfileResponse getUserProfile(Integer userId) {
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isEmpty()) {
            return null;
        }

        User user = userOptional.get();

        return new UserProfileResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getAddress(),
                user.getEmail(),
                user.getPhone(),
                user.getRole()
        );
    }

    public String updateUserProfile(Integer userId, UpdateProfileRequest request) {
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isEmpty()) {
            return "Потребителят не е намерен";
        }

        User user = userOptional.get();

        if (request.getFirstName() == null || request.getFirstName().isBlank()) {
            return "Името е задължително";
        }

        if (request.getLastName() == null || request.getLastName().isBlank()) {
            return "Фамилията е задължителна";
        }

        if (request.getAddress() == null || request.getAddress().isBlank()) {
            return "Адресът е задължителен";
        }

        if (request.getEmail() == null || request.getEmail().isBlank()) {
            return "Имейлът е задължителен";
        }

        String normalizedEmail = request.getEmail().trim().toLowerCase();

        Optional<User> existingByEmail = userRepository.findByEmail(normalizedEmail);
        if (existingByEmail.isPresent() && !existingByEmail.get().getId().equals(userId)) {
            return "Имейлът вече съществува";
        }

        user.setFirstName(request.getFirstName().trim());
        user.setLastName(request.getLastName().trim());
        user.setAddress(request.getAddress().trim());
        user.setEmail(normalizedEmail);
        user.setPhone(request.getPhone() != null ? request.getPhone().trim() : null);

        userRepository.save(user);

        return "Профилът е обновен успешно";
    }
}
