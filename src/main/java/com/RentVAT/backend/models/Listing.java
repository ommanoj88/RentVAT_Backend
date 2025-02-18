package com.RentVAT.backend.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Listing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(unique = true)
    private String productId;  // Unique product identifier

    private String title;
    private String description;
    private BigDecimal price;  // Total price (rent or buy)

    @ElementCollection
    private List<String> images;

    private boolean isAvailable;

    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    @CreationTimestamp
    private LocalDateTime createdAt;

    // Generate productId automatically upon creation
    @PrePersist
    public void generateProductId() {
        this.productId = "PROD" + System.currentTimeMillis(); // Or use UUID.randomUUID()
    }
}
