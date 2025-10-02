package com.RentVAT.backend.controllers;

import com.RentVAT.backend.dto.BookingEditRequestDTO;
import com.RentVAT.backend.dto.BookingRequestDTO;
import com.RentVAT.backend.dto.BookingResponseDTO;
import com.RentVAT.backend.models.*;
import com.RentVAT.backend.repository.BookingRepository;
import com.RentVAT.backend.repository.ListingRepository;
import com.RentVAT.backend.repository.UserRepository;
import com.RentVAT.backend.service.UserService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/bookings")
@CrossOrigin(origins = "http://localhost:3000")
public class BookingController {


    private final BookingRepository bookingRepository;
    private final ListingRepository listingRepository;
    private final UserRepository userRepository;
    private final UserService userService;


    public BookingController(BookingRepository bookingRepository, ListingRepository listingRepository, UserRepository userRepository,UserService userService) {
        this.bookingRepository = bookingRepository;
        this.listingRepository = listingRepository;
        this.userRepository = userRepository;
        this.userService=userService;
    }

    private User authenticateUser(String token) throws Exception {
        if (token == null || !token.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing or invalid Authorization header");
        }
        String idToken = token.replace("Bearer ", "").trim();
        idToken = idToken.replaceAll("^\"|\"$", "");
        FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
        String userUid = decodedToken.getUid();
        return userRepository.findByUid(userUid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }

    @GetMapping("/listing/{listingId}/bookings")
    public ResponseEntity<?> getBookingsForListing(@RequestHeader("Authorization") String token, @PathVariable Long listingId) {
        try {
            User owner = authenticateUser(token);
            Optional<Listing> listingOpt = listingRepository.findById(listingId);
            if (listingOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Invalid listing ID.");
            }
            Listing listing = listingOpt.get();
            if (!listing.getOwner().getId().equals(owner.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to view these bookings.");
            }
            List<Booking> bookings = bookingRepository.findByListingId(listingId);
            List<BookingResponseDTO> bookingResponses = bookings.stream().map(booking ->
                    new BookingResponseDTO(
                            booking.getId(),
                            booking.getRenter().getId(),
                            booking.getRenter().getUsername(),
                            booking.getRenter().getEmail(),
                            booking.getStartDate(),
                            booking.getEndDate(),
                            booking.getStatus().name(),
                            booking.getTotalPrice(),
                            booking.getRentalPrice(),
                            booking.getPlatformCommission(),
                            booking.isKycVerified(),
                            booking.getCreatedAt()
                    )
            ).collect(Collectors.toList());
            return ResponseEntity.ok(bookingResponses);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed: " + e.getMessage());
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> createBooking(@RequestHeader("Authorization") String token, @RequestBody BookingRequestDTO bookingRequest) {
        try {
            User renter = authenticateUser(token);
            Listing listing = listingRepository.findById(bookingRequest.getListingId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid listing ID."));

            // ✅ VALIDATION 1: Prevent users from renting own listings
            if (listing.getOwner().getId().equals(renter.getId())) {
                return ResponseEntity.badRequest().body("You cannot rent your own listing.");
            }

            // ✅ VALIDATION 2: Parse and validate dates
            LocalDate startDate;
            LocalDate endDate;
            try {
                startDate = LocalDate.parse(bookingRequest.getStartDate());
                endDate = LocalDate.parse(bookingRequest.getEndDate());
            } catch (Exception e) {
                return ResponseEntity.badRequest().body("Invalid date format. Use YYYY-MM-DD.");
            }

            // ✅ VALIDATION 3: Prevent past dates
            LocalDate today = LocalDate.now();
            if (startDate.isBefore(today)) {
                return ResponseEntity.badRequest().body("Start date cannot be in the past.");
            }

            // ✅ VALIDATION 4: Validate end date > start date
            if (endDate.isBefore(startDate) || endDate.isEqual(startDate)) {
                return ResponseEntity.badRequest().body("End date must be after start date.");
            }

            // ✅ VALIDATION 5: Check for date overlaps (prevent double booking)
            List<Booking> overlappingBookings = bookingRepository.findOverlappingBookings(
                listing.getId(), startDate, endDate
            );
            if (!overlappingBookings.isEmpty()) {
                return ResponseEntity.badRequest().body(
                    "This listing is already booked for the selected dates. Please choose different dates."
                );
            }

            // ✅ VALIDATION 6: Validate maximum rental duration (e.g., 90 days)
            long days = ChronoUnit.DAYS.between(startDate, endDate) + 1;
            if (days > 90) {
                return ResponseEntity.badRequest().body("Maximum rental duration is 90 days.");
            }

            // Calculate rental price and commission
            BigDecimal rentalPrice = calculatePrice(listing, days);
            BigDecimal platformCommission = rentalPrice.multiply(BigDecimal.valueOf(0.05)).max(BigDecimal.valueOf(50));
            BigDecimal totalAmount = rentalPrice.add(platformCommission);

            // Create booking with rental price included
            Booking booking = Booking.builder()
                    .listing(listing)
                    .renter(renter)
                    .startDate(startDate)
                    .endDate(endDate)
                    .rentalPrice(rentalPrice)
                    .platformCommission(platformCommission)
                    .totalPrice(totalAmount)
                    .status(Booking.BookingStatus.PENDING)
                    .build();

            bookingRepository.save(booking);

            return ResponseEntity.ok("Booking request submitted. Rental Price: Rs " + rentalPrice +
                    ", Platform Fee: Rs " + platformCommission + ", Total Amount: Rs " + totalAmount);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating booking: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/accept")
    public ResponseEntity<?> acceptBooking(@RequestHeader("Authorization") String token, @PathVariable Long id) {
        try {
            User owner = authenticateUser(token);
            Optional<Booking> bookingOpt = bookingRepository.findById(id);
            if (bookingOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Booking not found.");
            }
            Booking booking = bookingOpt.get();

            // Ensure only the listing owner can accept the booking
            if (!booking.getListing().getOwner().getId().equals(owner.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to accept this booking.");
            }

            booking.setStatus(Booking.BookingStatus.ACCEPTED);
            bookingRepository.save(booking);
            return ResponseEntity.ok("Booking accepted. Payment initiated. KYC process will start.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<?> rejectBooking(@RequestHeader("Authorization") String token, @PathVariable Long id) {
        try {
            User owner = authenticateUser(token);
            Optional<Booking> bookingOpt = bookingRepository.findById(id);
            if (bookingOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Booking not found.");
            }
            Booking booking = bookingOpt.get();

            // Ensure only the listing owner can reject the booking
            if (!booking.getListing().getOwner().getId().equals(owner.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to reject this booking.");
            }

            booking.setStatus(Booking.BookingStatus.REJECTED);
            bookingRepository.save(booking);
            return ResponseEntity.ok("Booking rejected.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<?> completeBooking(@RequestHeader("Authorization") String token, @PathVariable Long id) {
        try {
            User owner = authenticateUser(token);
            Optional<Booking> bookingOpt = bookingRepository.findById(id);
            if (bookingOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Booking not found.");
            }
            Booking booking = bookingOpt.get();

            // Ensure only the listing owner can complete the booking
            if (!booking.getListing().getOwner().getId().equals(owner.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to complete this booking.");
            }

            booking.setStatus(Booking.BookingStatus.COMPLETED);
            bookingRepository.save(booking);
            return ResponseEntity.ok("Booking completed. Payment released.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed: " + e.getMessage());
        }
    }

    @GetMapping("/my-bookings")
    public ResponseEntity<?> getUserBookings(@RequestHeader("Authorization") String token) {
        try {
            User renter = authenticateUser(token);
            List<Booking> bookings = bookingRepository.findByRenterId(renter.getId());

            List<BookingResponseDTO> bookingResponses = bookings.stream().map(booking ->
                    new BookingResponseDTO(
                            booking.getId(),
                            booking.getRenter().getId(),
                            booking.getRenter().getUsername(),
                            booking.getRenter().getEmail(),
                            booking.getStartDate(),
                            booking.getEndDate(),
                            booking.getStatus().name(),
                            booking.getTotalPrice(),
                            booking.getRentalPrice(),
                            booking.getPlatformCommission(),
                            booking.isKycVerified(),
                            booking.getCreatedAt()

                    )
            ).collect(Collectors.toList());

            return ResponseEntity.ok(bookingResponses);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed: " + e.getMessage());
        }
    }
    @PutMapping("/edit-booking/{bookingId}")
    public ResponseEntity<?> editBooking(
            @PathVariable Long bookingId,
            @RequestHeader("Authorization") String token,
            @RequestBody BookingEditRequestDTO editRequest) {
        try {
            User renter = authenticateUser(token);
            Optional<Booking> optionalBooking = bookingRepository.findById(bookingId);

            if (optionalBooking.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Booking not found");
            }

            Booking booking = optionalBooking.get();

            // ✅ Only allow edits if status is "PENDING"
            if (!booking.getStatus().equals(Booking.BookingStatus.PENDING)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Only pending bookings can be edited.");
            }

            // ✅ Ensure the user trying to edit is the renter
            if (!booking.getRenter().getId().equals(renter.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You can only edit your own bookings.");
            }

            // Update booking details
            booking.setStartDate(editRequest.getStartDate());
            booking.setEndDate(editRequest.getEndDate());
            booking.setTotalPrice(editRequest.getTotalPrice());

            bookingRepository.save(booking);

            return ResponseEntity.ok("Booking updated successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed: " + e.getMessage());
        }
    }

    @PutMapping("/cancel-booking/{bookingId}")
    public ResponseEntity<?> cancelBooking(
            @PathVariable Long bookingId,
            @RequestHeader("Authorization") String token) {
        try {
            User renter = authenticateUser(token);
            Optional<Booking> optionalBooking = bookingRepository.findById(bookingId);

            if (optionalBooking.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Booking not found");
            }

            Booking booking = optionalBooking.get();

            // ✅ Ensure the user trying to cancel is the renter
            if (!booking.getRenter().getId().equals(renter.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You can only cancel your own bookings.");
            }

            // ✅ Allow cancellation only if the booking is still pending
            if (!booking.getStatus().equals(Booking.BookingStatus.PENDING)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You can only cancel pending bookings.");
            }

            // ✅ Update status to "CANCELLED_BY_RENTER"
            booking.setStatus(Booking.BookingStatus.CANCELLED_BY_RENTER);
            bookingRepository.save(booking);

            return ResponseEntity.ok("Booking cancelled successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed: " + e.getMessage());
        }
    }


    private BigDecimal calculatePrice(Listing listing, long days) {
        BigDecimal totalPrice = BigDecimal.ZERO;
        long remainingDays = days;
        if (remainingDays >= 7) {
            totalPrice = listing.getPrice7Days().multiply(BigDecimal.valueOf(remainingDays / 7));
            remainingDays %= 7;
        }
        while (remainingDays >= 3) {
            totalPrice = totalPrice.add(listing.getPrice3Days());
            remainingDays -= 3;
        }
        if (remainingDays > 0) {
            totalPrice = totalPrice.add(listing.getPrice1Day().multiply(BigDecimal.valueOf(remainingDays)));
        }
        return totalPrice;
    }
}
