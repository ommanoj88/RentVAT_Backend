package com.RentVAT.backend.service;

import com.RentVAT.backend.models.KycVerification;
import com.RentVAT.backend.models.User;
import com.RentVAT.backend.repository.KycVerificationRepository;
import com.RentVAT.backend.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final KycVerificationRepository kycVerificationRepository;

    // ✅ Constructor Injection (Better than @Autowired)
    public UserService(UserRepository userRepository, KycVerificationRepository kycVerificationRepository) {
        this.userRepository = userRepository;
        this.kycVerificationRepository = kycVerificationRepository;
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserByUid(String uid) {
        return userRepository.findByUid(uid);
    }

    public void updateKycStatus(Long userId, boolean isVerified) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        user.setKycVerified(isVerified);
        userRepository.save(user);
    }

    public void startKycVerification(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // Check if user is already verified
        if (user.isKycVerified()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is already KYC verified.");
        }

        // Check if there's already an ongoing KYC verification request
        Optional<KycVerification> existingKyc = kycVerificationRepository.findByUserId(userId);
        if (existingKyc.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "KYC verification already initiated.");
        }

        // Create new KYC verification record
        KycVerification kycVerification = new KycVerification();
        kycVerification.setUser(user);
        kycVerification.setVerificationStatus("PENDING"); // Make sure KycVerification has this field

        kycVerificationRepository.save(kycVerification);
    }

}
