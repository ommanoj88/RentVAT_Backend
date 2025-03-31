package com.RentVAT.backend.dto;

public class BookmarkDTO {
    private Long listingId;

    // Constructor
    public BookmarkDTO(Long listingId) {
        this.listingId = listingId;
    }

    // Getter
    public Long getListingId() {
        return listingId;
    }
}
