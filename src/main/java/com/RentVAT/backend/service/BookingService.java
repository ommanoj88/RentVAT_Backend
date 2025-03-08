package com.RentVAT.backend.service;

import com.RentVAT.backend.models.*;
import com.RentVAT.backend.repository.BookingRepository;
import com.RentVAT.backend.repository.ListingRepository;
import com.RentVAT.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ListingRepository listingRepository;

    @Autowired
    private UserRepository userRepository;

    public Booking createBooking(Long renterId, Long listingId, LocalDate startDate, LocalDate endDate) throws Exception {
        User renter = userRepository.findById(renterId)
                .orElseThrow(() -> new Exception("Renter not found"));

        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new Exception("Listing not found"));

        if (listing.getOwner().getId().equals(renterId)) {
            throw new Exception("You cannot rent your own listing");
        }

        long days = ChronoUnit.DAYS.between(startDate, endDate);
        BigDecimal totalPrice = calculatePrice(listing, days);

        Booking booking = Booking.builder()
                .renter(renter)
                .listing(listing)
                .startDate(startDate)
                .endDate(endDate)
                .status(Booking.BookingStatus.PENDING)
                .totalPrice(totalPrice)
                .build();

        return bookingRepository.save(booking);
    }

    private BigDecimal calculatePrice(Listing listing, long days) {
        if (days <= 1) {
            return listing.getPrice1Day();
        } else if (days <= 3) {
            return listing.getPrice3Days();
        } else if (days <= 7) {
            return listing.getPrice7Days();
        } else {
            return listing.getPrice7Days().multiply(BigDecimal.valueOf(days / 7));
        }
    }

    public Optional<Booking> getBooking(Long id) {
        return bookingRepository.findById(id);
    }

    public Booking acceptBooking(Long bookingId) throws Exception {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new Exception("Booking not found"));

        booking.setStatus(Booking.BookingStatus.ACCEPTED);
        // TODO: Trigger payment here
        return bookingRepository.save(booking);
    }

    public Booking rejectBooking(Long bookingId) throws Exception {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new Exception("Booking not found"));

        booking.setStatus(Booking.BookingStatus.REJECTED);
        return bookingRepository.save(booking);
    }

    public Booking completeBooking(Long bookingId) throws Exception {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new Exception("Booking not found"));

        booking.setStatus(Booking.BookingStatus.COMPLETED);
        // TODO: Release payment here
        return bookingRepository.save(booking);
    }
}
