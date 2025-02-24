package com.RentVAT.backend.repository;

import com.RentVAT.backend.models.Listing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ListingRepository extends JpaRepository<Listing, Long> {
    List<Listing> findByAvailableForRentTrue(); // Find listings available for rent
    List<Listing> findByAvailableForSaleTrue(); // Find listings available for sale
}
