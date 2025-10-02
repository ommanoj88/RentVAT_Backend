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
    List<Listing> findByOwnerId(Long ownerId); // Find listings by owner ID

    @Query("SELECT l FROM Listing l WHERE " +
            "(COALESCE(:query, '') = '' OR LOWER(l.title) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(l.description) LIKE LOWER(CONCAT('%', :query, '%'))) " +
            "AND (:category IS NULL OR l.category = :category) " +
            "AND (COALESCE(:city, '') = '' OR LOWER(l.city) = LOWER(:city)) " +  // ✅ Handles NULL city
            "AND (l.price1Day BETWEEN COALESCE(:minPrice, 0) AND COALESCE(:maxPrice, 9999999)) " +  // ✅ Handles NULL price
            "AND (" +
            "    (:rent = TRUE AND l.availableForRent = TRUE) " +
            "    OR (:sale = TRUE AND l.availableForSale = TRUE) " +
            ")"
    )
    Page<Listing> searchListings(
            @Param("query") String query,
            @Param("category") com.RentVAT.backend.model.Category category,
            @Param("city") String city,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("rent") Boolean rent,
            @Param("sale") Boolean sale,
            Pageable pageable);



}
