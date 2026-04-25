package com.shredshare.ShredShare.Controller;

import com.shredshare.ShredShare.Service.WardrobeService;
import com.shredshare.ShredShare.dto.Admin.AdminDetailsResponse;
import com.shredshare.ShredShare.dto.Admin.AdminStatsResponse;
import com.shredshare.ShredShare.dto.Admin.AdminWardrobeRequest;
import com.shredshare.ShredShare.dto.Wardrobe.CreateWardrobeResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    private final WardrobeService wardrobeService;

    public AdminController(WardrobeService wardrobeService) {
        this.wardrobeService = wardrobeService;
    }

    @GetMapping("/wardrobes/pending")
    public ResponseEntity<List<AdminWardrobeRequest>> getPendingWardrobes() {
        return ResponseEntity.ok(wardrobeService.getPendingWardrobes());
    }

    @PutMapping("/wardrobes/{id}/approve")
    public ResponseEntity<CreateWardrobeResponse> approveWardrobe(@PathVariable Integer id) {
        return ResponseEntity.ok(wardrobeService.approveWardrobe(id));
    }

    @PutMapping("/wardrobes/{id}/reject")
    public ResponseEntity<CreateWardrobeResponse> rejectWardrobe(@PathVariable Integer id) {
        return ResponseEntity.ok(wardrobeService.rejectWardrobe(id));
    }

    @GetMapping("/stats")
    public ResponseEntity<AdminStatsResponse> getAdminStats() {
        return ResponseEntity.ok(wardrobeService.getAdminStats());
    }

    @GetMapping("/wardrobes/{id}")
    public ResponseEntity<AdminDetailsResponse> getWardrobeDetails(@PathVariable Integer id) {
        return ResponseEntity.ok(wardrobeService.getAdminDetails(id));
    }
}
