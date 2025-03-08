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
    // Automatically set createdAt before saving to DB
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }


    @Enumerated(EnumType.STRING)
    private com.RentVAT.backend.model.Category category;

    public BigDecimal getPrice1Day() {
        return price1Day;
    }

    public void setPrice1Day(BigDecimal price1Day) {
        this.price1Day = price1Day;
    }

    public BigDecimal getPrice3Days() {
        return price3Days;
    }

    public void setPrice3Days(BigDecimal price3Days) {
        this.price3Days = price3Days;
    }

    public BigDecimal getPrice7Days() {
        return price7Days;
    }

    public void setPrice7Days(BigDecimal price7Days) {
        this.price7Days = price7Days;
    }

    public BigDecimal getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(BigDecimal salePrice) {
        this.salePrice = salePrice;
    }

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
}
