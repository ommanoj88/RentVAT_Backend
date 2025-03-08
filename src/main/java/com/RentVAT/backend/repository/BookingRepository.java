package com.RentVAT.backend.repository;

import com.RentVAT.backend.models.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByListingOwnerId(Long ownerId);
    List<Booking> findByListingId(Long listingId);
}
