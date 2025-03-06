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
            @RequestParam(required = false) com.RentVAT.backend.model.Category category,
            @RequestParam(required = false) String city,
            @RequestParam(defaultValue = "0") String minPrice,
            @RequestParam(defaultValue = "1000000") String maxPrice,
            @RequestParam(defaultValue = "true") Boolean rent,
            @RequestParam(defaultValue = "true") Boolean sale,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {


        // ✅ Convert minPrice and maxPrice from String to BigDecimal
        BigDecimal minPriceValue = new BigDecimal(minPrice);
        BigDecimal maxPriceValue = new BigDecimal(maxPrice);

        // ✅ Convert sorting and pagination parameters into Pageable
        Sort.Direction direction = sortDirection.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));


        Page<ListingDTO> listings = listingService.searchListings(query, category, city,
                minPriceValue, maxPriceValue, rent, sale, pageable);

        return ResponseEntity.ok(listings);
    }

}
