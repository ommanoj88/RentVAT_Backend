package com.RentVAT.backend.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @Column(unique = true, nullable = false)
    private String uid; // Firebase UID (New field added)

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Transient
    private String password;
 // Consider hashing this in a real application

    @JsonIgnore
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    private List<Listing> listings; // List of items listed by the user

    @Column(nullable = false)
    private boolean kycVerified = false; // New field for KYC status

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private KycVerification kycVerification;

}

