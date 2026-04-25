package com.shredshare.ShredShare.dto.Owner;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OwnerWardrobeDto {
    private Integer wardrobeId;
    private String name;
    private String resortName;
    private String address;
    private String phone;
    private String description;
    private String imageUrl;
    private String status;
}