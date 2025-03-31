package com.RentVAT.backend.models;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "kyc_verifications")
public class KycVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true) // One KYC per user
    private User user;

    private String documentType; // e.g., "Passport", "Driving License"
    private String documentUrl; // URL to uploaded KYC document
    private boolean verified; // KYC verification status
    private String verificationStatus; // e.g., "PENDING", "APPROVED", "REJECTED"
    private LocalDateTime submittedAt;
    private LocalDateTime verifiedAt;
}
