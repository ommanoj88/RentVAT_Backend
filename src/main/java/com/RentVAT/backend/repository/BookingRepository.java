package com.RentVAT.backend.repository;

import com.RentVAT.backend.models.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByListingOwnerId(Long ownerId);
    List<Booking> findByListingId(Long listingId);
    List<Booking> findByRenterId(Long renterId);

    // ✅ NEW: Check for date overlaps to prevent double booking
    @Query("SELECT b FROM Booking b WHERE b.listing.id = :listingId " +
           "AND b.status NOT IN ('REJECTED', 'CANCELLED_BY_RENTER') " +
           "AND ((b.startDate <= :endDate AND b.endDate >= :startDate))")
    List<Booking> findOverlappingBookings(
        @Param("listingId") Long listingId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
}
