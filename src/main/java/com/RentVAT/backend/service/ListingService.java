package com.RentVAT.backend.service;

import com.RentVAT.backend.dto.ListingDTO;
import com.RentVAT.backend.models.Listing;
import com.RentVAT.backend.models.User;
import com.RentVAT.backend.repository.ListingRepository;
import com.RentVAT.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class ListingService {

    @Autowired
    private ListingRepository listingRepository;

    @Autowired
    private UserRepository userRepository; // ✅ Inject UserRepository

    public Listing createListing(Listing listing) {
        if (listing.getOwner() == null || listing.getOwner().getId() == null) {
            throw new IllegalArgumentException("Owner must be specified");
        }

        // ✅ Use the injected userRepository instance
        User owner = userRepository.findById(listing.getOwner().getId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid owner ID"));

        listing.setOwner(owner);  // Explicitly set owner
        return listingRepository.save(listing);
    }

    public Optional<Listing> getListingById(Long id) {
        return listingRepository.findById(id);
    }

    public List<Listing> getAllListings() {
        return listingRepository.findAll();
    }

    public List<Listing> getAvailableForRent() {
        return listingRepository.findByAvailableForRentTrue();
    }

    public List<Listing> getAvailableForSale() {
        return listingRepository.findByAvailableForSaleTrue();
    }

    public Page<ListingDTO> searchListings(String query, String category, String city,
                                           BigDecimal minPrice, BigDecimal maxPrice,
                                           boolean rent, boolean sale, Pageable pageable) {

        Page<Listing> listings = listingRepository.searchListings(query, category, city,
                minPrice, maxPrice,
                rent, sale, pageable);

        // Convert Listing to ListingDTO
        return listings.map(listing -> new ListingDTO(
                listing.getId(),
                listing.getTitle(),
                listing.getPrice()

        ));
    }




}
