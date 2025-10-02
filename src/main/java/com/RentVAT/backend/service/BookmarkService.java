package com.RentVAT.backend.service;

import com.RentVAT.backend.dto.BookmarkDTO;
import com.RentVAT.backend.dto.ListingDTO;
import com.RentVAT.backend.models.Bookmark;
import com.RentVAT.backend.models.Listing;
import com.RentVAT.backend.models.User;
import com.RentVAT.backend.repository.BookmarkRepository;
import com.RentVAT.backend.repository.UserRepository;
import com.RentVAT.backend.repository.ListingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookmarkService {

    @Autowired
    private BookmarkRepository bookmarkRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ListingRepository listingRepository;

    public void addBookmark(Long userId, BookmarkDTO bookmarkDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Listing listing = listingRepository.findById(bookmarkDTO.getListingId())
                .orElseThrow(() -> new RuntimeException("Listing not found"));

        // ✅ Check if bookmark already exists
        if (bookmarkRepository.existsByUserAndListing(user, listing)) {
            throw new RuntimeException("Bookmark already exists");
        }

        Bookmark bookmark = new Bookmark(user, listing);
        bookmarkRepository.save(bookmark);
    }

    public List<ListingDTO> getAllBookmarkedListings(Long userId) {
        List<Bookmark> bookmarks = bookmarkRepository.findByUserId(userId);
        return bookmarks.stream()
                .map(bookmark -> {
                    Listing listing = bookmark.getListing();
                    return new ListingDTO(
                            listing.getId(),
                            listing.getTitle(),
                            listing.getDescription(),
                            listing.getAddress(),
                            listing.getCity(),
                            listing.getCategory() != null ? listing.getCategory().toString() : null,
                            listing.getPrice1Day(),
                            listing.getPrice3Days(),
                            listing.getPrice7Days(),
                            listing.getSalePrice(),
                            listing.isAvailableForRent(),
                            listing.isAvailableForSale(),
                            listing.getCreatedAt(),
                            listing.getOwner() != null ? listing.getOwner().getId() : null,
                            listing.getOwner() != null ? listing.getOwner().getUsername() : null
                    );
                })
                .toList();
    }
}
