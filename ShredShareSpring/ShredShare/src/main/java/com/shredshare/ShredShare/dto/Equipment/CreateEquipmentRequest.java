package com.shredshare.ShredShare.dto.Equipment;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

@Data
public class CreateEquipmentRequest {
    private Integer wardrobeId;
    private String name;
    private String type;
    private String brand;
    private String size;
    private BigDecimal pricePerDay;
    private Integer quantity;
    private String description;
    private MultipartFile image;
}