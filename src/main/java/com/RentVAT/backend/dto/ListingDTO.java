package com.RentVAT.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ListingDTO {
    private Long id;
    private String title;
    private String description;
    private String address;
    private String city;
    private String category;
    private BigDecimal price1Day;
    private BigDecimal price3Days;
    private BigDecimal price7Days;
    private BigDecimal salePrice;
    private boolean availableForRent;
    private boolean availableForSale;
    private LocalDateTime createdAt;
    private Long ownerId;
    private String ownerName;

    // ✅ FIXED: Full constructor with all fields
    public ListingDTO(Long id, String title, String description, String address, String city,
                     String category, BigDecimal price1Day, BigDecimal price3Days,
                     BigDecimal price7Days, BigDecimal salePrice, boolean availableForRent,
                     boolean availableForSale, LocalDateTime createdAt, Long ownerId, String ownerName) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.address = address;
        this.city = city;
        this.category = category;
        this.price1Day = price1Day;
        this.price3Days = price3Days;
        this.price7Days = price7Days;
        this.salePrice = salePrice;
        this.availableForRent = availableForRent;
        this.availableForSale = availableForSale;
        this.createdAt = createdAt;
        this.ownerId = ownerId;
        this.ownerName = ownerName;
    }

    // ✅ Getters
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getAddress() { return address; }
    public String getCity() { return city; }
    public String getCategory() { return category; }
    public BigDecimal getPrice1Day() { return price1Day; }
    public BigDecimal getPrice3Days() { return price3Days; }
    public BigDecimal getPrice7Days() { return price7Days; }
    public BigDecimal getSalePrice() { return salePrice; }
    public boolean isAvailableForRent() { return availableForRent; }
    public boolean isAvailableForSale() { return availableForSale; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public Long getOwnerId() { return ownerId; }
    public String getOwnerName() { return ownerName; }
}
