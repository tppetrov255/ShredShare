package com.shredshare.ShredShare.dto.Booking;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class BookingResponse {
    private boolean success;
    private String message;
    private Integer bookingId;
    private BigDecimal totalPrice;
}