package com.shredshare.ShredShare.Service;

import com.shredshare.ShredShare.Entity.EnumWardrobeStatus;
import com.shredshare.ShredShare.Entity.Resort;
import com.shredshare.ShredShare.Entity.User;
import com.shredshare.ShredShare.Entity.Wardrobe;
import com.shredshare.ShredShare.Repository.ResortRepository;
import com.shredshare.ShredShare.Repository.UserRepository;
import com.shredshare.ShredShare.Repository.WardrobeRepository;
import com.shredshare.ShredShare.dto.Admin.AdminDetailsResponse;
import com.shredshare.ShredShare.dto.Admin.AdminStatsResponse;
import com.shredshare.ShredShare.dto.Admin.AdminWardrobeRequest;
import com.shredshare.ShredShare.dto.Wardrobe.CreateWardrobeResponse;
import com.shredshare.ShredShare.dto.Owner.OwnerWardrobeDto;
import com.shredshare.ShredShare.dto.Wardrobe.WardrobeListEquipmentDto;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class WardrobeService {

    private final WardrobeRepository wardrobeRepository;
    private final UserRepository userRepository;
    private final ResortRepository resortRepository;

    public WardrobeService(
            WardrobeRepository wardrobeRepository,
            UserRepository userRepository,
            ResortRepository resortRepository
    ) {
        this.wardrobeRepository = wardrobeRepository;
        this.userRepository = userRepository;
        this.resortRepository = resortRepository;
    }

    public java.util.List<AdminWardrobeRequest> getPendingWardrobes() {
        return wardrobeRepository.findByStatus(EnumWardrobeStatus.PENDING)
                .stream()
                .map(wardrobe -> new AdminWardrobeRequest(
                        wardrobe.getWardrobeId(),
                        wardrobe.getName(),
                        wardrobe.getOwner() != null ? wardrobe.getOwner().getFirstName()  + " " + wardrobe.getOwner().getLastName() : "",
                        wardrobe.getResort() != null ? wardrobe.getResort().getName() : "",
                        wardrobe.getPhone(),
                        wardrobe.getAddress(),
                        wardrobe.getDescription(),
                        wardrobe.getImageUrl(),
                        wardrobe.getStatus().name()
                ))
                .toList();
    }

    public CreateWardrobeResponse approveWardrobe(Integer wardrobeId) {
        Optional<Wardrobe> wardrobeOptional = wardrobeRepository.findById(wardrobeId);

        if (wardrobeOptional.isEmpty()) {
            return new CreateWardrobeResponse(false, "Гардеробът не е намерен");
        }

        Wardrobe wardrobe = wardrobeOptional.get();
        wardrobe.setStatus(EnumWardrobeStatus.APPROVED);
        wardrobeRepository.save(wardrobe);

        return new CreateWardrobeResponse(true, "Гардеробът беше одобрен успешно");
    }

    public CreateWardrobeResponse rejectWardrobe(Integer wardrobeId) {
        Optional<Wardrobe> wardrobeOptional = wardrobeRepository.findById(wardrobeId);

        if (wardrobeOptional.isEmpty()) {
            return new CreateWardrobeResponse(false, "Гардеробът не е намерен");
        }

        Wardrobe wardrobe = wardrobeOptional.get();
        wardrobe.setStatus(EnumWardrobeStatus.REJECTED);
        wardrobeRepository.save(wardrobe);

        return new CreateWardrobeResponse(true, "Гардеробът беше отхвърлен успешно");
    }

    public CreateWardrobeResponse createWardrobe(
            Integer ownerId,
            Integer resortId,
            String name,
            String address,
            String phone,
            String description,
            Double latitude,
            Double longitude,
            MultipartFile image
    ) {
        if (ownerId == null) {
            return new CreateWardrobeResponse(false, "Липсва ID на собственика");
        }

        if (resortId == null) {
            return new CreateWardrobeResponse(false, "Липсва ID на курорта");
        }

        if (name == null || name.isBlank()) {
            return new CreateWardrobeResponse(false, "Името на гардероба е задължително");
        }

        Optional<User> ownerOptional = userRepository.findById(ownerId);
        if (ownerOptional.isEmpty()) {
            return new CreateWardrobeResponse(false, "Собственикът не е намерен");
        }

        Optional<Resort> resortOptional = resortRepository.findById(resortId);
        if (resortOptional.isEmpty()) {
            return new CreateWardrobeResponse(false, "Курортът не е намерен");
        }

        String imagePath = null;

        try {
            if (image != null && !image.isEmpty()) {
                String uploadDir = System.getProperty("user.dir") + File.separator + "uploads" + File.separator + "wardrobes";
                File dir = new File(uploadDir);

                if (!dir.exists() && !dir.mkdirs()) {
                    return new CreateWardrobeResponse(false, "Неуспешно създаване на папка за снимките");
                }

                String originalFilename = image.getOriginalFilename();
                String safeFileName = (originalFilename != null && !originalFilename.isBlank())
                        ? originalFilename
                        : "image.jpg";

                String fileName = UUID.randomUUID() + "_" + safeFileName;
                File destination = new File(dir, fileName);

                image.transferTo(destination);

                imagePath = "/uploads/wardrobes/" + fileName;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new CreateWardrobeResponse(false, "Грешка при качване на снимката: " + e.getMessage());
        }

        Wardrobe wardrobe = new Wardrobe();
        wardrobe.setOwner(ownerOptional.get());
        wardrobe.setResort(resortOptional.get());
        wardrobe.setName(name);
        wardrobe.setAddress(address);
        wardrobe.setPhone(phone);
        wardrobe.setDescription(description);

        if (latitude != null) {
            wardrobe.setLatitude(java.math.BigDecimal.valueOf(latitude));
        }

        if (longitude != null) {
            wardrobe.setLongitude(java.math.BigDecimal.valueOf(longitude));
        }

        wardrobe.setImageUrl(imagePath);
        wardrobe.setStatus(EnumWardrobeStatus.PENDING);
        wardrobeRepository.save(wardrobe);

        return new CreateWardrobeResponse(true, "Заявката за гардероб е изпратена успешно и очаква одобрение");
    }

    public AdminStatsResponse getAdminStats() {

        int totalPending = wardrobeRepository.countByStatus(EnumWardrobeStatus.PENDING);
        int totalApproved = wardrobeRepository.countByStatus(EnumWardrobeStatus.APPROVED);

        int totalOwners = userRepository.countByRole("owner");
        int totalUsers = (int) userRepository.count();

        return new AdminStatsResponse(
                totalPending,
                totalApproved,
                totalOwners,
                totalUsers
        );
    }

    public List<OwnerWardrobeDto> getWardrobesByOwner(Integer ownerId) {
        return wardrobeRepository.findByOwner_Id(ownerId)
                .stream()
                .map(wardrobe -> new OwnerWardrobeDto(
                        wardrobe.getWardrobeId(),
                        wardrobe.getName(),
                        wardrobe.getResort() != null ? wardrobe.getResort().getName() : "",
                        wardrobe.getAddress(),
                        wardrobe.getPhone(),
                        wardrobe.getDescription(),
                        wardrobe.getImageUrl(),
                        wardrobe.getStatus().name()
                ))
                .toList();
    }

    public List<WardrobeListEquipmentDto> getApprovedWardrobesByResort(Integer resortId) {
        return wardrobeRepository
                .findByResort_ResortIdAndStatus(resortId, EnumWardrobeStatus.APPROVED)
                .stream()
                .map(wardrobe -> new WardrobeListEquipmentDto(
                        wardrobe.getWardrobeId(),
                        wardrobe.getName(),
                        wardrobe.getResort() != null ? wardrobe.getResort().getName() : "",
                        wardrobe.getAddress(),
                        wardrobe.getPhone(),
                        wardrobe.getDescription(),
                        wardrobe.getImageUrl(),
                        wardrobe.getLatitude() != null ? wardrobe.getLatitude().doubleValue() : null,
                        wardrobe.getLongitude() != null ? wardrobe.getLongitude().doubleValue() : null
                ))
                .toList();
    }

    public AdminDetailsResponse getAdminDetails(Integer wardrobeId) {
        Wardrobe wardrobe = wardrobeRepository.findById(wardrobeId)
                .orElseThrow(() -> new RuntimeException("Wardrobe not found"));

        User owner = wardrobe.getOwner();
        Resort resort = wardrobe.getResort();

        return new AdminDetailsResponse(
                wardrobe.getWardrobeId(),
                wardrobe.getName(),
                resort != null ? resort.getName() : "",
                wardrobe.getPhone(),
                wardrobe.getAddress(),
                wardrobe.getDescription(),
                wardrobe.getImageUrl(),
                wardrobe.getStatus().name(),

                owner != null ? owner.getId() : null,
                owner != null ? owner.getFirstName() : "",
                owner != null ? owner.getLastName() : "",
                owner != null ? owner.getEmail() : "",
                owner != null ? owner.getPhone() : "",
                owner != null && owner.getRole() != null ? owner.getRole() : ""
        );
    }
}