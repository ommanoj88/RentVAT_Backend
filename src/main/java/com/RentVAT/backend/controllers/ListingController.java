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
@CrossOrigin(origins = "http://localhost:3000")
public class ListingController {

    @Autowired
    private ListingService listingService;

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<?> createListing(@RequestHeader("Authorization") String token, @RequestBody Listing listing) {
        try {
            // **Step 1: Validate the Authorization header**
            if (token == null || !token.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid Authorization header");
            }

            // **Step 2: Extract the token and remove extra quotes**
            String idToken = token.replace("Bearer ", "").trim();
            idToken = idToken.replaceAll("^\"|\"$", "");

            System.out.println("Cleaned Token: " + idToken);

            // **Step 3: Verify Firebase Token**
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
            String userUid = decodedToken.getUid();

            // **Step 4: Find the user in the database**
            Optional<User> userOptional = userService.getUserByUid(userUid);
            if (userOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
            }
            User user = userOptional.get();

            // ✅ **NEW: Validate listing data**
            if (listing.getTitle() == null || listing.getTitle().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Title is required");
            }
            if (listing.getDescription() == null || listing.getDescription().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Description is required");
            }
            if (listing.getCity() == null || listing.getCity().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("City is required");
            }
            if (listing.getAddress() == null || listing.getAddress().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Address is required");
            }
            if (listing.getPrice1Day() == null || listing.getPrice1Day().compareTo(java.math.BigDecimal.ZERO) <= 0) {
                return ResponseEntity.badRequest().body("Valid price for 1 day is required");
            }

            // ✅ **NEW: Ensure price3Days and price7Days default to price1Day if not set**
            if (listing.getPrice3Days() == null || listing.getPrice3Days().compareTo(java.math.BigDecimal.ZERO) <= 0) {
                listing.setPrice3Days(listing.getPrice1Day().multiply(java.math.BigDecimal.valueOf(3)));
            }
            if (listing.getPrice7Days() == null || listing.getPrice7Days().compareTo(java.math.BigDecimal.ZERO) <= 0) {
                listing.setPrice7Days(listing.getPrice1Day().multiply(java.math.BigDecimal.valueOf(7)));
            }

            // **Step 5: Set the owner of the listing and save it**
            listing.setOwner(user);
            Listing createdListing = listingService.createListing(listing);

            return new ResponseEntity<>(createdListing, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Validation error: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating listing: " + e.getMessage());
        }
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> updateListing(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id,
            @RequestBody Listing updatedListing) {
        try {
            // **Step 1: Validate the Authorization header**
            if (token == null || !token.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid Authorization header");
            }

            // **Step 2: Extract and clean the token**
            String idToken = token.replace("Bearer ", "").trim();
            idToken = idToken.replaceAll("^\"|\"$", "");

            // **Step 3: Verify Firebase Token**
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
            String userUid = decodedToken.getUid();

            // **Step 4: Find the user in the database**
            Optional<User> userOptional = userService.getUserByUid(userUid);
            if (userOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
            }
            User user = userOptional.get();

            // **Step 5: Find the existing listing**
            Optional<Listing> existingListingOptional = listingService.getListingById(id);
            if (existingListingOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Listing not found");
            }

            Listing existingListing = existingListingOptional.get();

            // **Step 6: Ensure the user owns the listing**
            if (!existingListing.getOwner().getId().equals(user.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to update this listing");
            }

            // **Step 7: Update the listing details**
            existingListing.setTitle(updatedListing.getTitle());
            existingListing.setDescription(updatedListing.getDescription());
            existingListing.setAddress(updatedListing.getAddress());
            existingListing.setCity(updatedListing.getCity());
            existingListing.setCategory(updatedListing.getCategory());
            existingListing.setPrice1Day(updatedListing.getPrice1Day());
            existingListing.setPrice3Days(updatedListing.getPrice3Days());
            existingListing.setPrice7Days(updatedListing.getPrice7Days());
            existingListing.setAvailableForRent(updatedListing.isAvailableForRent());
            existingListing.setAvailableForSale(updatedListing.isAvailableForSale());

            // **Step 8: Save the updated listing**
            Listing savedListing = listingService.updateListing(existingListing);

            return ResponseEntity.ok(savedListing);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating listing: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteListing(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id) {
        try {
            // **Step 1: Validate the Authorization header**
            if (token == null || !token.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid Authorization header");
            }

            // **Step 2: Extract and clean the token**
            String idToken = token.replace("Bearer ", "").trim();
            idToken = idToken.replaceAll("^\"|\"$", "");

            // **Step 3: Verify Firebase Token**
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
            String userUid = decodedToken.getUid();

            // **Step 4: Find the user in the database**
            Optional<User> userOptional = userService.getUserByUid(userUid);
            if (userOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
            }
            User user = userOptional.get();

            // **Step 5: Find the listing by ID**
            Optional<Listing> existingListingOptional = listingService.getListingById(id);
            if (existingListingOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Listing not found");
            }

            Listing existingListing = existingListingOptional.get();

            // **Step 6: Ensure the user owns the listing**
            if (!existingListing.getOwner().getId().equals(user.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to delete this listing");
            }

            // **Step 7: Delete the listing**
            listingService.deleteListingById(id);

            return ResponseEntity.ok("Listing deleted successfully");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting listing: " + e.getMessage());
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

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Listing>> getListingsByUserId(@PathVariable Long userId) {
        List<Listing> listings = listingService.getListingsByOwnerId(userId);
        return ResponseEntity.ok(listings);
    }
}
