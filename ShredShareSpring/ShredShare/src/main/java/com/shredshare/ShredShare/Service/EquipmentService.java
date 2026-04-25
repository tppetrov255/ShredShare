package com.shredshare.ShredShare.Service;

import com.shredshare.ShredShare.Entity.Equipment;
import com.shredshare.ShredShare.Entity.Wardrobe;
import com.shredshare.ShredShare.Repository.EquipmentRepository;
import com.shredshare.ShredShare.Repository.WardrobeRepository;
import com.shredshare.ShredShare.dto.Equipment.EquipmentResponseDto;
import com.shredshare.ShredShare.dto.Wardrobe.CreateWardrobeResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class EquipmentService {

    private final EquipmentRepository equipmentRepository;
    private final WardrobeRepository wardrobeRepository;

    public EquipmentService(
            EquipmentRepository equipmentRepository,
            WardrobeRepository wardrobeRepository
    ) {
        this.equipmentRepository = equipmentRepository;
        this.wardrobeRepository = wardrobeRepository;
    }

    public CreateWardrobeResponse createEquipment(
            Integer wardrobeId,
            String name,
            String type,
            String brand,
            String size,
            BigDecimal pricePerDay,
            Integer quantity,
            String description,
            MultipartFile image
    ) {
        if (wardrobeId == null) {
            return new CreateWardrobeResponse(false, "Липсва wardrobeId");
        }

        if (name == null || name.isBlank()) {
            return new CreateWardrobeResponse(false, "Името на артикула е задължително");
        }

        Optional<Wardrobe> wardrobeOptional = wardrobeRepository.findById(wardrobeId);
        if (wardrobeOptional.isEmpty()) {
            return new CreateWardrobeResponse(false, "Гардеробът не е намерен");
        }

        if (quantity != null && quantity < 0) {
            return new CreateWardrobeResponse(false, "Количеството не може да е отрицателно");
        }

        if (pricePerDay != null && pricePerDay.compareTo(BigDecimal.ZERO) < 0) {
            return new CreateWardrobeResponse(false, "Цената не може да е отрицателна");
        }

        String imagePath = null;

        try {
            if (image != null && !image.isEmpty()) {
                String uploadDir = System.getProperty("user.dir")
                        + File.separator + "uploads"
                        + File.separator + "equipment";

                File dir = new File(uploadDir);

                if (!dir.exists() && !dir.mkdirs()) {
                    return new CreateWardrobeResponse(false, "Неуспешно създаване на папка за снимките");
                }

                String originalFilename = image.getOriginalFilename();
                String safeFileName = (originalFilename != null && !originalFilename.isBlank())
                        ? originalFilename
                        : "equipment.jpg";

                String fileName = UUID.randomUUID() + "_" + safeFileName;
                File destination = new File(dir, fileName);

                image.transferTo(destination);
                imagePath = "/uploads/equipment/" + fileName;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new CreateWardrobeResponse(false, "Грешка при качване на снимката: " + e.getMessage());
        }

        Equipment equipment = new Equipment();
        equipment.setWardrobe(wardrobeOptional.get());
        equipment.setName(name);
        equipment.setType(type);
        equipment.setBrand(brand);
        equipment.setSize(size);
        equipment.setPricePerDay(pricePerDay);
        equipment.setQuantity(quantity);
        equipment.setDescription(description);
        equipment.setImageUrl(imagePath);

        equipmentRepository.save(equipment);

        return new CreateWardrobeResponse(true, "Артикулът е добавен успешно");
    }

    public List<EquipmentResponseDto> getEquipmentByWardrobe(Integer wardrobeId) {
        return equipmentRepository.findByWardrobe_WardrobeId(wardrobeId)
                .stream()
                .map(eq -> new EquipmentResponseDto(
                        eq.getEquipmentId(),
                        eq.getWardrobe().getWardrobeId(),
                        eq.getName(),
                        eq.getType(),
                        eq.getBrand(),
                        eq.getSize(),
                        eq.getPricePerDay(),
                        eq.getQuantity(),
                        eq.getDescription(),
                        eq.getImageUrl()
                ))
                .toList();
    }

    public EquipmentResponseDto getEquipmentById(Integer equipmentId) {
        Equipment equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new RuntimeException("Артикулът не е намерен"));

        return new EquipmentResponseDto(
                equipment.getEquipmentId(),
                equipment.getWardrobe().getWardrobeId(),
                equipment.getName(),
                equipment.getType(),
                equipment.getBrand(),
                equipment.getSize(),
                equipment.getPricePerDay(),
                equipment.getQuantity(),
                equipment.getDescription(),
                equipment.getImageUrl()
        );
    }

    public CreateWardrobeResponse updateEquipment(
            Integer equipmentId,
            String name,
            String type,
            String brand,
            String size,
            BigDecimal pricePerDay,
            Integer quantity,
            String description,
            MultipartFile image
    ) {
        Optional<Equipment> equipmentOptional = equipmentRepository.findById(equipmentId);

        if (equipmentOptional.isEmpty()) {
            return new CreateWardrobeResponse(false, "Артикулът не е намерен");
        }

        if (name == null || name.isBlank()) {
            return new CreateWardrobeResponse(false, "Името на артикула е задължително");
        }

        if (quantity != null && quantity < 0) {
            return new CreateWardrobeResponse(false, "Количеството не може да е отрицателно");
        }

        if (pricePerDay != null && pricePerDay.compareTo(BigDecimal.ZERO) < 0) {
            return new CreateWardrobeResponse(false, "Цената не може да е отрицателна");
        }

        Equipment equipment = equipmentOptional.get();

        String imagePath = equipment.getImageUrl();

        try {
            if (image != null && !image.isEmpty()) {
                String uploadDir = System.getProperty("user.dir")
                        + File.separator + "uploads"
                        + File.separator + "equipment";

                File dir = new File(uploadDir);

                if (!dir.exists() && !dir.mkdirs()) {
                    return new CreateWardrobeResponse(false, "Неуспешно създаване на папка за снимките");
                }

                String originalFilename = image.getOriginalFilename();
                String safeFileName = (originalFilename != null && !originalFilename.isBlank())
                        ? originalFilename
                        : "equipment.jpg";

                String fileName = UUID.randomUUID() + "_" + safeFileName;
                File destination = new File(dir, fileName);

                image.transferTo(destination);
                imagePath = "/uploads/equipment/" + fileName;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new CreateWardrobeResponse(false, "Грешка при качване на снимката: " + e.getMessage());
        }

        equipment.setName(name);
        equipment.setType(type);
        equipment.setBrand(brand);
        equipment.setSize(size);
        equipment.setPricePerDay(pricePerDay);
        equipment.setQuantity(quantity);
        equipment.setDescription(description);
        equipment.setImageUrl(imagePath);

        equipmentRepository.save(equipment);

        return new CreateWardrobeResponse(true, "Артикулът е редактиран успешно");
    }

    public CreateWardrobeResponse deleteEquipment(Integer equipmentId) {
        Optional<Equipment> equipmentOptional = equipmentRepository.findById(equipmentId);

        if (equipmentOptional.isEmpty()) {
            return new CreateWardrobeResponse(false, "Артикулът не е намерен");
        }

        equipmentRepository.delete(equipmentOptional.get());
        return new CreateWardrobeResponse(true, "Артикулът е изтрит успешно");
    }
}