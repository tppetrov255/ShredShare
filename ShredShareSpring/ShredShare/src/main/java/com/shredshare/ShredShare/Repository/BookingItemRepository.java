package com.shredshare.ShredShare.Repository;

import com.shredshare.ShredShare.Entity.BookingItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingItemRepository extends JpaRepository<BookingItem, Integer> {
}
