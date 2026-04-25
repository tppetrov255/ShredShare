package com.shredshare.ShredShare.dto.Booking;

import lombok.Data;

@Data
public class CreateBookingItemRequest {
    private Integer equipmentId;
    private Integer quantity;
}