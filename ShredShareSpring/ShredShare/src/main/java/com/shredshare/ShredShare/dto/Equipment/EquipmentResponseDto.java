package com.shredshare.ShredShare.dto.Equipment;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;


@Data
@AllArgsConstructor
public class EquipmentResponseDto {
    private Integer equipmentId;
    private Integer wardrobeId;
    private String name;
    private String type;
    private String brand;
    private String size;
    private BigDecimal pricePerDay;
    private Integer quantity;
    private String description;
    private String imageUrl;
}
