package com.RentVAT.backend.controllers;

import com.RentVAT.backend.models.Listing;
import com.RentVAT.backend.models.User;
import com.RentVAT.backend.service.ListingService;
import com.RentVAT.backend.service.UserService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import jakarta.servlet.http.HttpServletRequest;
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

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<?> createListing(@RequestHeader("Authorization") String token, @RequestBody Listing listing) {
        try {
            // Verify Firebase Token
            String idToken = token.replace("Bearer ", "");
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
            String userUid = decodedToken.getUid();

            // Find the User by UID
            Optional<User> userOptional = userService.getUserByUid(userUid);
            if (userOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
            }
            User user = userOptional.get();

            // Assign the authenticated user as the owner of the listing
            listing.setOwner(user);
            Listing createdListing = listingService.createListing(listing);

            return new ResponseEntity<>(createdListing, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
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
