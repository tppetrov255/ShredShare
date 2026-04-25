package com.shredshare.ShredShare.dto.Wardrobe;

public class WardrobeListEquipmentDto {
    private Integer wardrobeId;
    private String name;
    private String resortName;
    private String address;
    private String phone;
    private String description;
    private String imageUrl;
    private Double latitude;
    private Double longitude;

    public WardrobeListEquipmentDto(
            Integer wardrobeId,
            String name,
            String resortName,
            String address,
            String phone,
            String description,
            String imageUrl,
            Double latitude,
            Double longitude
    ) {
        this.wardrobeId = wardrobeId;
        this.name = name;
        this.resortName = resortName;
        this.address = address;
        this.phone = phone;
        this.description = description;
        this.imageUrl = imageUrl;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Integer getWardrobeId() {
        return wardrobeId;
    }

    public String getName() {
        return name;
    }

    public String getResortName() {
        return resortName;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }
}