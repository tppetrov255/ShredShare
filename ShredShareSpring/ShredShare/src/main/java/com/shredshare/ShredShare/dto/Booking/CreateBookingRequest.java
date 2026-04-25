package com.shredshare.ShredShare.dto.Booking;

import lombok.Data;

import java.util.List;

@Data
public class CreateBookingRequest {
    private Integer userId;
    private String startDate;
    private String endDate;
    private List<CreateBookingItemRequest> items;
}
