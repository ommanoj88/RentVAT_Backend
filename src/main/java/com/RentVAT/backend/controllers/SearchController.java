package com.RentVAT.backend.controllers;

import com.RentVAT.backend.dto.ListingDTO;
import com.RentVAT.backend.models.Listing;
import com.RentVAT.backend.service.ListingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    @Autowired
    private ListingService listingService;

    @GetMapping("/listings")
    public ResponseEntity<Page<ListingDTO>> searchListings(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String city,
            @RequestParam(defaultValue = "0") BigDecimal minPrice,
            @RequestParam(defaultValue = "1000000") BigDecimal maxPrice,
            @RequestParam(defaultValue = "true") boolean rent,
            @RequestParam(defaultValue = "true") boolean sale,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {

        // ✅ Convert sorting and pagination parameters into Pageable
        Sort sort = sortDirection.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ListingDTO> listings = listingService.searchListings(query, category, city,
                minPrice, maxPrice, rent, sale, pageable);

        return ResponseEntity.ok(listings);

    }
}
