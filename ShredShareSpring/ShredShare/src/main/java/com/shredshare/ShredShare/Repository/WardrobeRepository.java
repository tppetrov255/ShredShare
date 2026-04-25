package com.shredshare.ShredShare.Repository;

import com.shredshare.ShredShare.Entity.EnumWardrobeStatus;
import com.shredshare.ShredShare.Entity.Wardrobe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WardrobeRepository extends JpaRepository<Wardrobe, Integer> {
    List<Wardrobe> findByOwner_Id(Integer ownerId);
    List<Wardrobe> findByResort_ResortId(Integer resortId);
    List<Wardrobe> findByStatus(EnumWardrobeStatus status);
    int countByStatus(EnumWardrobeStatus status);
    List<Wardrobe> findByResort_ResortIdAndStatus(Integer resortId, EnumWardrobeStatus status);
}