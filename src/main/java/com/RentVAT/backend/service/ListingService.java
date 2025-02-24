package com.RentVAT.backend.service;

import com.RentVAT.backend.models.Listing;
import com.RentVAT.backend.repository.ListingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ListingService {

    @Autowired
    private ListingRepository listingRepository;

    public Listing createListing(Listing listing) {
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
}
