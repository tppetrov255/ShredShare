package com.shredshare.ShredShare.Service;

import com.shredshare.ShredShare.Entity.*;
import com.shredshare.ShredShare.Repository.BookingRepository;
import com.shredshare.ShredShare.Repository.EquipmentRepository;
import com.shredshare.ShredShare.Repository.UserRepository;
import com.shredshare.ShredShare.dto.Booking.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final EquipmentRepository equipmentRepository;

    public BookingService(
            BookingRepository bookingRepository,
            UserRepository userRepository,
            EquipmentRepository equipmentRepository
    ) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.equipmentRepository = equipmentRepository;
    }

    public BookingResponse createBooking(CreateBookingRequest request) {
        try {
            if (request.getUserId() == null) {
                return new BookingResponse(false, "Липсва userId.", null, null);
            }

            if (request.getItems() == null || request.getItems().isEmpty()) {
                return new BookingResponse(false, "Количката е празна.", null, null);
            }

            LocalDate startDate = LocalDate.parse(request.getStartDate());
            LocalDate endDate = LocalDate.parse(request.getEndDate());

            if (endDate.isBefore(startDate)) {
                return new BookingResponse(false, "Крайната дата не може да е преди началната.", null, null);
            }

            long days = ChronoUnit.DAYS.between(startDate, endDate) + 1;
            if (days <= 0) {
                return new BookingResponse(false, "Невалиден период.", null, null);
            }

            Optional<User> userOptional = userRepository.findById(request.getUserId());
            if (userOptional.isEmpty()) {
                return new BookingResponse(false, "Потребителят не е намерен.", null, null);
            }

            User user = userOptional.get();

            Booking booking = new Booking();
            booking.setUser(user);
            booking.setStartDate(startDate);
            booking.setEndDate(endDate);
            booking.setStatus(BookingStatus.PENDING);

            List<BookingItem> bookingItems = new ArrayList<>();
            BigDecimal totalPrice = BigDecimal.ZERO;

            for (CreateBookingItemRequest itemRequest : request.getItems()) {
                if (itemRequest.getEquipmentId() == null || itemRequest.getQuantity() == null || itemRequest.getQuantity() <= 0) {
                    return new BookingResponse(false, "Невалиден артикул в заявката.", null, null);
                }

                Optional<Equipment> equipmentOptional = equipmentRepository.findById(itemRequest.getEquipmentId());
                if (equipmentOptional.isEmpty()) {
                    return new BookingResponse(false, "Артикул с ID " + itemRequest.getEquipmentId() + " не е намерен.", null, null);
                }

                Equipment equipment = equipmentOptional.get();

                if (equipment.getPricePerDay() == null) {
                    return new BookingResponse(false, "Артикулът " + equipment.getName() + " няма цена.", null, null);
                }

                if (equipment.getQuantity() == null || equipment.getQuantity() < itemRequest.getQuantity()) {
                    return new BookingResponse(false, "Няма достатъчно количество за " + equipment.getName() + ".", null, null);
                }

                BookingItem bookingItem = new BookingItem();
                bookingItem.setBooking(booking);
                bookingItem.setEquipment(equipment);
                bookingItem.setQuantity(itemRequest.getQuantity());
                bookingItem.setUnitPrice(equipment.getPricePerDay());

                bookingItems.add(bookingItem);

                BigDecimal itemTotal = equipment.getPricePerDay()
                        .multiply(BigDecimal.valueOf(itemRequest.getQuantity()))
                        .multiply(BigDecimal.valueOf(days));

                totalPrice = totalPrice.add(itemTotal);
            }

            booking.setTotalPrice(totalPrice);
            booking.setItems(bookingItems);

            Booking savedBooking = bookingRepository.save(booking);

            return new BookingResponse(
                    true,
                    "Резервацията е създадена успешно.",
                    savedBooking.getBookingId(),
                    savedBooking.getTotalPrice()
            );

        } catch (Exception e) {
            return new BookingResponse(false, "Грешка при създаване на резервацията.", null, null);
        }
    }

    public List<UserBookingResponse> getBookingsByUser(Integer userId) {
        List<Booking> bookings = bookingRepository.findByUser_IdOrderByBookingIdDesc(userId);

        List<UserBookingResponse> result = new ArrayList<>();

        for (Booking booking : bookings) {
            List<UserBookingItemResponse> itemResponses = new ArrayList<>();

            if (booking.getItems() != null) {
                for (BookingItem item : booking.getItems()) {
                    itemResponses.add(
                            new UserBookingItemResponse(
                                    item.getEquipment().getEquipmentId(),
                                    item.getEquipment().getName(),
                                    item.getQuantity(),
                                    item.getUnitPrice()
                            )
                    );
                }
            }

            result.add(
                    new UserBookingResponse(
                            booking.getBookingId(),
                            booking.getStartDate(),
                            booking.getEndDate(),
                            booking.getTotalPrice(),
                            booking.getStatus().name(),
                            itemResponses
                    )
            );
        }

        return result;
    }

    public BookingResponse cancelBooking(Integer bookingId, Integer userId) {
        try {
            Optional<Booking> bookingOptional = bookingRepository.findById(bookingId);

            if (bookingOptional.isEmpty()) {
                return new BookingResponse(false, "Резервацията не е намерена.", null, null);
            }

            Booking booking = bookingOptional.get();

            if (booking.getUser() == null || !booking.getUser().getId().equals(userId)) {
                return new BookingResponse(false, "Нямате право да отмените тази резервация.", null, null);
            }

            BookingStatus currentStatus = booking.getStatus();

            if (currentStatus == BookingStatus.CANCELLED) {
                return new BookingResponse(false, "Резервацията вече е отменена.", booking.getBookingId(), booking.getTotalPrice());
            }

            if (currentStatus == BookingStatus.REJECTED) {
                return new BookingResponse(false, "Отказана резервация не може да бъде отменена отново.", booking.getBookingId(), booking.getTotalPrice());
            }

            if (currentStatus == BookingStatus.COMPLETED) {
                return new BookingResponse(false, "Завършена резервация не може да бъде отменена.", booking.getBookingId(), booking.getTotalPrice());
            }

            if (currentStatus == BookingStatus.PICKED_UP) {
                return new BookingResponse(false, "Взета резервация не може да бъде отменена.", booking.getBookingId(), booking.getTotalPrice());
            }

            booking.setStatus(BookingStatus.CANCELLED);
            bookingRepository.save(booking);

            return new BookingResponse(
                    true,
                    "Резервацията беше отменена успешно.",
                    booking.getBookingId(),
                    booking.getTotalPrice()
            );
        } catch (Exception e) {
            return new BookingResponse(false, "Грешка при отказване на резервацията.", null, null);
        }
    }

    public List<OwnerBookingResponse> getBookingsByOwner(Integer ownerId) {
        List<Booking> bookings = bookingRepository.findBookingsByOwnerId(ownerId);

        List<OwnerBookingResponse> result = new ArrayList<>();

        for (Booking booking : bookings) {
            List<OwnerBookingItemResponse> itemResponses = new ArrayList<>();

            if (booking.getItems() != null) {
                for (BookingItem item : booking.getItems()) {
                    Equipment equipment = item.getEquipment();

                    if (equipment != null &&
                            equipment.getWardrobe() != null &&
                            equipment.getWardrobe().getOwner() != null &&
                            equipment.getWardrobe().getOwner().getId().equals(ownerId)) {

                        itemResponses.add(
                                new OwnerBookingItemResponse(
                                        equipment.getEquipmentId(),
                                        equipment.getName(),
                                        item.getQuantity(),
                                        item.getUnitPrice(),
                                        equipment.getWardrobe().getName()
                                )
                        );
                    }
                }
            }

            User customer = booking.getUser();

            result.add(
                    new OwnerBookingResponse(
                            booking.getBookingId(),
                            customer != null ? customer.getId() : null,
                            customer != null ? customer.getFirstName() : "-",
                            customer != null ? customer.getLastName() : "-",
                            customer != null ? customer.getEmail() : "-",
                            customer != null ? customer.getPhone() : "-",
                            customer != null ? customer.getAddress() : "-",
                            booking.getStartDate(),
                            booking.getEndDate(),
                            booking.getTotalPrice(),
                            booking.getStatus().name(),
                            itemResponses
                    )
            );
        }

        return result;
    }

    private boolean ownerCanAccessBooking(Booking booking, Integer ownerId) {
        if (booking.getItems() == null) return false;

        for (BookingItem item : booking.getItems()) {
            if (item.getEquipment() != null &&
                    item.getEquipment().getWardrobe() != null &&
                    item.getEquipment().getWardrobe().getOwner() != null &&
                    item.getEquipment().getWardrobe().getOwner().getId().equals(ownerId)) {
                return true;
            }
        }

        return false;
    }

    private boolean isValidOwnerStatusTransition(BookingStatus currentStatus, BookingStatus newStatus) {
        if (currentStatus == null || newStatus == null) return false;

        return switch (currentStatus) {
            case PENDING -> newStatus == BookingStatus.APPROVED || newStatus == BookingStatus.REJECTED;
            case APPROVED -> newStatus == BookingStatus.PICKED_UP;
            case PICKED_UP -> newStatus == BookingStatus.COMPLETED;
            default -> false;
        };
    }

    public BookingResponse updateBookingStatusByOwner(Integer bookingId, Integer ownerId, String newStatusText) {
        try {
            Optional<Booking> bookingOptional = bookingRepository.findById(bookingId);

            if (bookingOptional.isEmpty()) {
                return new BookingResponse(false, "Резервацията не е намерена.", null, null);
            }

            Booking booking = bookingOptional.get();

            if (!ownerCanAccessBooking(booking, ownerId)) {
                return new BookingResponse(false, "Нямате право да променяте тази резервация.", null, null);
            }

            BookingStatus newStatus;
            try {
                newStatus = BookingStatus.valueOf(newStatusText.toUpperCase());
            } catch (Exception e) {
                return new BookingResponse(false, "Невалиден статус.", booking.getBookingId(), booking.getTotalPrice());
            }

            BookingStatus currentStatus = booking.getStatus();

            if (!isValidOwnerStatusTransition(currentStatus, newStatus)) {
                return new BookingResponse(
                        false,
                        "Невалиден преход от " + currentStatus + " към " + newStatus + ".",
                        booking.getBookingId(),
                        booking.getTotalPrice()
                );
            }

            booking.setStatus(newStatus);
            bookingRepository.save(booking);

            return new BookingResponse(
                    true,
                    "Статусът беше обновен успешно на " + newStatus + ".",
                    booking.getBookingId(),
                    booking.getTotalPrice()
            );

        } catch (Exception e) {
            return new BookingResponse(false, "Грешка при промяна на статуса.", null, null);
        }
    }
}