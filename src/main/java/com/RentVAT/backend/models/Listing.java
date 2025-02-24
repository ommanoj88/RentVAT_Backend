package com.RentVAT.backend.models;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

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
    private String description;

    @Enumerated(EnumType.STRING)
    private com.RentVAT.backend.model.Category category;

    private BigDecimal price; // Unified price for renting and buying
    private boolean availableForRent;
    private boolean availableForSale;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private com.RentVAT.backend.models.User owner; // Reference to the user listing the item
}
