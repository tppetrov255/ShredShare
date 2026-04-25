package com.shredshare.ShredShare.dto.Booking;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class UserBookingItemResponse {
    private Integer equipmentId;
    private String equipmentName;
    private Integer quantity;
    private BigDecimal unitPrice;
}