package com.RentVAT.backend.models;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "listings")
public class Listing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")  // Ensures no bytea type issues
    private String description;

    private String address;
    private String city;
    @Column(updatable = false) // Prevent updates
    private LocalDateTime createdAt;
    // Automatically set createdAt before saving to DB
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }


    @Enumerated(EnumType.STRING)
    private com.RentVAT.backend.model.Category category;

    @Column(nullable = false)
    private BigDecimal price; // Unified price for renting and buying
    private boolean availableForRent;
    private boolean availableForSale;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private com.RentVAT.backend.models.User owner; // Reference to the user listing the item
}
