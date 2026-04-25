package com.shredshare.ShredShare.Repository;

import com.shredshare.ShredShare.Entity.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EquipmentRepository extends JpaRepository<Equipment, Integer> {
    List<Equipment> findByWardrobe_WardrobeId(Integer wardrobeId);
}