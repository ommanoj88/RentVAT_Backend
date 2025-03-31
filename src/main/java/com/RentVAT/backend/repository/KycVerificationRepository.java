package com.RentVAT.backend.repository;

import com.RentVAT.backend.models.KycVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface KycVerificationRepository extends JpaRepository<KycVerification, Long> {
    Optional<KycVerification> findByUserId(Long userId);
}
