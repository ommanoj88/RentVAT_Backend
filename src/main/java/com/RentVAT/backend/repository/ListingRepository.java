package com.RentVAT.backend.repository;

import com.RentVAT.backend.models.Listing;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ListingRepository extends JpaRepository<Listing, Long> {
    List<Listing> findByAvailableForRentTrue(); // Find listings available for rent
    List<Listing> findByAvailableForSaleTrue(); // Find listings available for sale

    @Query("SELECT l FROM Listing l WHERE " +
            "(:query IS NULL OR LOWER(l.title) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(l.description) LIKE LOWER(CONCAT('%', :query, '%'))) " +
            "AND (:category IS NULL OR l.category = :category) " +
            "AND (:city IS NULL OR LOWER(l.city) = LOWER(:city)) " +
            "AND (:minPrice IS NULL OR l.price >= :minPrice) " +
            "AND (:maxPrice IS NULL OR l.price <= :maxPrice) " +
            "AND (:rent IS NULL OR :rent = false OR l.availableForRent = true) " +
            "AND (:sale IS NULL OR :sale = false OR l.availableForSale = true)")
    Page<Listing> searchListings(String query, String category, String city,
                                 BigDecimal minPrice, BigDecimal maxPrice,
                                 Boolean rent, Boolean sale, Pageable pageable);

}
