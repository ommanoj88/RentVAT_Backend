package com.RentVAT.backend.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
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

    @Enumerated(EnumType.STRING)
    private com.RentVAT.backend.model.Category category;

    @Column(nullable = false)
    private BigDecimal price1Day; // Price for 1-day rental
    private BigDecimal price3Days; // Price for 3-day rental
    private BigDecimal price7Days; // Price for 7-day rental (weekly rate)
    private BigDecimal salePrice; // Price for buying the item

    private boolean availableForRent;
    private boolean availableForSale;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private com.RentVAT.backend.models.User owner; // Reference to the user listing the item

    @OneToMany(mappedBy = "listing", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Booking> bookings; // List of bookings associated with this listing

    // ✅ Combined: onCreate and validation in single @PrePersist method
    @PrePersist
    protected void onPrePersist() {
        this.createdAt = LocalDateTime.now();
        validatePrices();
    }

    // ✅ Validation on update as well
    @PreUpdate
    protected void onPreUpdate() {
        validatePrices();
    }

    // ✅ Price validation method
    private void validatePrices() {
        if (price1Day != null && price1Day.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price for 1 day must be positive");
        }
        if (price3Days != null && price3Days.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price for 3 days must be positive");
        }
        if (price7Days != null && price7Days.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price for 7 days must be positive");
        }
        if (salePrice != null && salePrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Sale price must be positive");
        }
    }
}
