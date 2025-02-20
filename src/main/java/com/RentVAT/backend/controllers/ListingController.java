package com.RentVAT.backend.controllers;

import com.RentVAT.backend.models.Listing;
import com.RentVAT.backend.repositories.ListingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/listings")
public class ListingController {

    @Autowired
    private ListingRepository listingRepository;

    // ✅ Public: Get all listings (Browsing allowed)
    @GetMapping
    public List<Listing> getAllListings() {
        return listingRepository.findAll();
    }

    // ✅ Public: Get a single listing
    @GetMapping("/{id}")
    public Listing getListingById(@PathVariable Long id) {
        return listingRepository.findById(id).orElse(null);
    }

    // 🔒 Protected: Create a listing (Login required)
    @PostMapping("/create")
    public Listing createListing(@RequestBody Listing listing) {
        // TODO: Add authentication check before saving
        return listingRepository.save(listing);
    }

    // 🔒 Protected: Rent or Buy an item (Login required)
    @PostMapping("/{id}/rent")
    public String rentItem(@PathVariable Long id) {
        // TODO: Verify user authentication before allowing rental
        return "Item rented successfully!";
    }
}
