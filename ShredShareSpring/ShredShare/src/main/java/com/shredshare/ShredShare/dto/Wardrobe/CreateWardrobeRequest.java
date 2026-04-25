package com.shredshare.ShredShare.dto.Wardrobe;

import lombok.Data;

import java.math.BigDecimal;


@Data
public class CreateWardrobeRequest {

    private Integer ownerId;
    private Integer resortId;
    private String name;
    private String address;
    private String phone;
    private String description;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String imageUrl;

}
