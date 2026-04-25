package com.shredshare.ShredShare.dto.Admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminDetailsResponse {

    private Integer wardrobeId;
    private String wardrobeName;
    private String resortName;
    private String phone;
    private String address;
    private String description;
    private String imageUrl;
    private String status;

    private Integer ownerId;
    private String ownerFirstName;
    private String ownerLastName;
    private String ownerEmail;
    private String ownerPhone;
    private String ownerRole;
}
