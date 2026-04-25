package com.shredshare.ShredShare.dto.Booking;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
public class UserBookingResponse {
    private Integer bookingId;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal totalPrice;
    private String status;
    private List<UserBookingItemResponse> items;
}
