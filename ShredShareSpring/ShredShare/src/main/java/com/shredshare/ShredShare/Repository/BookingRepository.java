package com.shredshare.ShredShare.Repository;

import com.shredshare.ShredShare.Entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    List<Booking> findByUser_IdOrderByBookingIdDesc(Integer userId);

    @Query("""
        SELECT DISTINCT b
        FROM Booking b
        JOIN b.items bi
        JOIN bi.equipment e
        JOIN e.wardrobe w
        WHERE w.owner.id = :ownerId
        ORDER BY b.bookingId DESC
    """)
    List<Booking> findBookingsByOwnerId(Integer ownerId);
}