package com.shredshare.ShredShare.dto.Booking;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
public class OwnerBookingResponse {
    private Integer bookingId;

    private Integer customerId;
    private String customerFirstName;
    private String customerLastName;
    private String customerEmail;
    private String customerPhone;
    private String customerAddress;

    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal totalPrice;
    private String status;

    private List<OwnerBookingItemResponse> items;
}