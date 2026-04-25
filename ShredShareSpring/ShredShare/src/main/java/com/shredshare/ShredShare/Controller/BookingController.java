package com.shredshare.ShredShare.Controller;

import com.shredshare.ShredShare.Service.BookingService;
import com.shredshare.ShredShare.dto.Booking.BookingResponse;
import com.shredshare.ShredShare.dto.Booking.CreateBookingRequest;
import com.shredshare.ShredShare.dto.Booking.OwnerBookingResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.shredshare.ShredShare.dto.Booking.UserBookingResponse;
import org.springframework.web.bind.annotation.RequestParam;
import com.shredshare.ShredShare.dto.Booking.UpdateBookingStatusRequest;
import java.util.List;

@RestController
@RequestMapping("/bookings")
@CrossOrigin(origins = "*")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping("/create")
    public ResponseEntity<BookingResponse> createBooking(
            @RequestBody CreateBookingRequest request
    ) {
        return ResponseEntity.ok(bookingService.createBooking(request));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserBookingResponse>> getBookingsByUser(
            @PathVariable Integer userId
    ) {
        return ResponseEntity.ok(bookingService.getBookingsByUser(userId));
    }

    @PutMapping("/cancel/{bookingId}")
    public ResponseEntity<BookingResponse> cancelBooking(
            @PathVariable Integer bookingId,
            @RequestParam Integer userId
    ) {
        return ResponseEntity.ok(bookingService.cancelBooking(bookingId, userId));
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<OwnerBookingResponse>> getBookingsByOwner(
            @PathVariable Integer ownerId
    ) {
        return ResponseEntity.ok(bookingService.getBookingsByOwner(ownerId));
    }

    @PutMapping("/owner/{bookingId}/status")
    public ResponseEntity<BookingResponse> updateBookingStatusByOwner(
            @PathVariable Integer bookingId,
            @RequestBody UpdateBookingStatusRequest request
    ) {
        return ResponseEntity.ok(
                bookingService.updateBookingStatusByOwner(
                        bookingId,
                        request.getOwnerId(),
                        request.getNewStatus()
                )
        );
    }

}