package com.RentVAT.backend.models;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Bookmark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "listing_id", nullable = false)
    private Listing listing;

    // Constructor, getters, and setters
    public Bookmark(User user, Listing listing) {
        this.user = user;
        this.listing = listing;
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Listing getListing() {
        return listing;
    }
}
