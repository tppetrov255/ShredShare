package com.shredshare.ShredShare.dto.Admin;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AdminWardrobeRequest {
    private Integer wardrobeId;
    private String wardrobeName;
    private String ownerName;
    private String resortName;
    private String phone;
    private String address;
    private String description;
    private String imageUrl;
    private String status;
}
