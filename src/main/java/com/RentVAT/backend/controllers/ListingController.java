package com.RentVAT.backend.controllers;

import com.RentVAT.backend.models.Listing;
import com.RentVAT.backend.service.ListingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/listings")
public class ListingController {

    @Autowired
    private ListingService listingService;

    @PostMapping
    public ResponseEntity<Listing> createListing(@RequestBody Listing listing) {
        Listing createdListing = listingService.createListing(listing);
        return new ResponseEntity<>(createdListing, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Listing> getListingById(@PathVariable Long id) {
        Optional<Listing> listing = listingService.getListingById(id);
        return listing.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Listing>> getAllListings() {
        List<Listing> listings = listingService.getAllListings();
        return ResponseEntity.ok(listings);
    }

    @GetMapping("/rent")
    public ResponseEntity<List<Listing>> getAvailableForRent() {
        List<Listing> listings = listingService.getAvailableForRent();
        return ResponseEntity.ok(listings);
    }

    @GetMapping("/sale")
    public ResponseEntity<List<Listing>> getAvailableForSale() {
        List<Listing> listings = listingService.getAvailableForSale();
        return ResponseEntity.ok(listings);
    }
}
