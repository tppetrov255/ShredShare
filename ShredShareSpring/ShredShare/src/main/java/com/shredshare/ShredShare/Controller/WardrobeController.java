package com.shredshare.ShredShare.Controller;

import com.shredshare.ShredShare.Service.WardrobeService;
import com.shredshare.ShredShare.dto.Wardrobe.CreateWardrobeResponse;
import com.shredshare.ShredShare.dto.Owner.OwnerWardrobeDto;
import com.shredshare.ShredShare.dto.Wardrobe.WardrobeListEquipmentDto;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/wardrobes")
@CrossOrigin(origins = "*")
public class WardrobeController {

    private final WardrobeService wardrobeService;

    public WardrobeController(WardrobeService wardrobeService) {
        this.wardrobeService = wardrobeService;
    }

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CreateWardrobeResponse> createWardrobe(
            @RequestParam("ownerId") Integer ownerId,
            @RequestParam("resortId") Integer resortId,
            @RequestParam("name") String name,
            @RequestParam("address") String address,
            @RequestParam("phone") String phone,
            @RequestParam("description") String description,
            @RequestParam("latitude") Double latitude,
            @RequestParam("longitude") Double longitude,
            @RequestParam(value = "imageUrl", required = false) MultipartFile imageUrl
    ) {
        CreateWardrobeResponse response = wardrobeService.createWardrobe(
                ownerId, resortId, name, address, phone, description, latitude, longitude, imageUrl
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<OwnerWardrobeDto>> getWardrobesByOwner(@PathVariable Integer ownerId) {
        return ResponseEntity.ok(wardrobeService.getWardrobesByOwner(ownerId));
    }

    @GetMapping("/resort/{resortId}")
    public ResponseEntity<List<WardrobeListEquipmentDto>> getApprovedWardrobesByResort(
            @PathVariable Integer resortId
    ) {
        return ResponseEntity.ok(wardrobeService.getApprovedWardrobesByResort(resortId));
    }
}
