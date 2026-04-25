package com.shredshare.ShredShare.Controller;

import com.shredshare.ShredShare.Service.EquipmentService;
import com.shredshare.ShredShare.dto.Equipment.EquipmentResponseDto;
import com.shredshare.ShredShare.dto.Wardrobe.CreateWardrobeResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/equipment")
@CrossOrigin(origins = "*")
public class EquipmentController {

    private final EquipmentService equipmentService;

    public EquipmentController(EquipmentService equipmentService) {
        this.equipmentService = equipmentService;
    }

    @PostMapping("/create")
    public ResponseEntity<CreateWardrobeResponse> createEquipment(
            @RequestParam Integer wardrobeId,
            @RequestParam String name,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String size,
            @RequestParam(required = false) BigDecimal pricePerDay,
            @RequestParam(required = false) Integer quantity,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) MultipartFile image
    ) {
        return ResponseEntity.ok(
                equipmentService.createEquipment(
                        wardrobeId,
                        name,
                        type,
                        brand,
                        size,
                        pricePerDay,
                        quantity,
                        description,
                        image
                )
        );
    }

    @GetMapping("/wardrobe/{wardrobeId}")
    public ResponseEntity<List<EquipmentResponseDto>> getEquipmentByWardrobe(
            @PathVariable Integer wardrobeId
    ) {
        return ResponseEntity.ok(equipmentService.getEquipmentByWardrobe(wardrobeId));
    }

    @GetMapping("/{equipmentId}")
    public ResponseEntity<EquipmentResponseDto> getEquipmentById(
            @PathVariable Integer equipmentId
    ) {
        return ResponseEntity.ok(equipmentService.getEquipmentById(equipmentId));
    }

    @PutMapping(value = "/update/{equipmentId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CreateWardrobeResponse> updateEquipment(
            @PathVariable Integer equipmentId,
            @RequestParam String name,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String size,
            @RequestParam(required = false) BigDecimal pricePerDay,
            @RequestParam(required = false) Integer quantity,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) MultipartFile image
    ) {
        return ResponseEntity.ok(
                equipmentService.updateEquipment(
                        equipmentId,
                        name,
                        type,
                        brand,
                        size,
                        pricePerDay,
                        quantity,
                        description,
                        image
                )
        );
    }

    @DeleteMapping("/delete/{equipmentId}")
    public ResponseEntity<CreateWardrobeResponse> deleteEquipment(
            @PathVariable Integer equipmentId
    ) {
        return ResponseEntity.ok(equipmentService.deleteEquipment(equipmentId));
    }
}