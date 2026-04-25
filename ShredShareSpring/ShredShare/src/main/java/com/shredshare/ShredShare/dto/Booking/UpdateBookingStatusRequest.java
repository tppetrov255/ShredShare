package com.shredshare.ShredShare.dto.Booking;

import lombok.Data;

@Data
public class UpdateBookingStatusRequest {
    private Integer ownerId;
    private String newStatus;
}