package com.shredshare.ShredShare.Controller;

import com.shredshare.ShredShare.Service.UserService;
import com.shredshare.ShredShare.dto.Admin.SimpleResponse;
import com.shredshare.ShredShare.dto.Profile.UpdateProfileRequest;
import com.shredshare.ShredShare.dto.Profile.UserProfileResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserProfile(@PathVariable Integer userId) {
        UserProfileResponse response = userService.getUserProfile(userId);

        if (response == null) {
            return ResponseEntity.badRequest()
                    .body(new SimpleResponse(false, "Потребителят не е намерен"));
        }

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<SimpleResponse> updateUserProfile(
            @PathVariable Integer userId,
            @RequestBody UpdateProfileRequest request
    ) {
        String message = userService.updateUserProfile(userId, request);

        if (message.equals("Профилът е обновен успешно")) {
            return ResponseEntity.ok(new SimpleResponse(true, message));
        } else {
            return ResponseEntity.badRequest().body(new SimpleResponse(false, message));
        }
    }
}