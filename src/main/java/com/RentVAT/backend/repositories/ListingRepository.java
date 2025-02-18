package com.RentVAT.backend.repositories;

import com.RentVAT.backend.models.Listing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ListingRepository extends JpaRepository<Listing, Long> {
    // You can add custom queries here if needed
}
