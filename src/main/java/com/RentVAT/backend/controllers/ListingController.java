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

    // Create a new listing
    @PostMapping
    public Listing createListing(@RequestBody Listing listing) {
        return listingRepository.save(listing);
    }

    // Get all listings
    @GetMapping
    public List<Listing> getAllListings() {
        return listingRepository.findAll();
    }

    // Get a listing by its productId
    @GetMapping("/{productId}")
    public Listing getListingByProductId(@PathVariable String productId) {
        return listingRepository.findById(Long.parseLong(productId)).orElse(null);
    }
}
