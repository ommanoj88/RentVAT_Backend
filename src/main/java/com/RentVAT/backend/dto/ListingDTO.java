package com.RentVAT.backend.dto;

import java.math.BigDecimal;

public class ListingDTO {
    private Long id;
    private String title;
    private BigDecimal price;
    private String photoUrl;

    // Constructor
    public ListingDTO(Long id, String title, BigDecimal price) {
        this.id = id;
        this.title = title;
        this.price = price;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }
}
