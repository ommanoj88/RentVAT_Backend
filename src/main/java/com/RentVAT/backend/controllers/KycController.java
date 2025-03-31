package com.RentVAT.backend.controllers;

import com.RentVAT.backend.models.User;
import com.RentVAT.backend.repository.UserRepository;
import com.RentVAT.backend.service.UserService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("api/kyc")
public class KycController {

    private final UserRepository userRepository;
    private final UserService userService;

    public KycController(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    private User authenticateUser(String token) throws Exception {
        if (token == null || !token.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing or invalid Authorization header");
        }
        String idToken = token.replace("Bearer ", "").trim();
        idToken = idToken.replaceAll("^\"|\"$", "");
        FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
        String userUid = decodedToken.getUid();
        return userRepository.findByUid(userUid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }

    /**
     * Check if the authenticated user is KYC verified.
     */
    @GetMapping("/status")
    public ResponseEntity<?> checkKycStatus(@RequestHeader("Authorization") String token) {
        try {
            User user = authenticateUser(token);
            return ResponseEntity.ok(Map.of("kycVerified", user.isKycVerified()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed: " + e.getMessage());
        }
    }

    /**
     * Initiate KYC verification for the authenticated user.
     */
    @PostMapping("/initiate")
    public ResponseEntity<?> initiateKyc(@RequestHeader("Authorization") String token) {
        try {
            User user = authenticateUser(token);

            if (user.isKycVerified()) {
                return ResponseEntity.badRequest().body("User is already KYC verified.");
            }

            // Corrected method call
            userService.startKycVerification(user.getId()); // ✅ Use instance method

            return ResponseEntity.ok("KYC process initiated.");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed: " + e.getMessage());
        }
    }

    /**
     * Webhook to receive KYC verification status updates from an external provider.
     */
    @PostMapping("/kyc/webhook")
    public ResponseEntity<?> handleKycWebhook(@RequestBody Map<String, Object> webhookData) {
        Long userId = Long.valueOf(webhookData.get("userId").toString());
        String verificationStatus = webhookData.get("status").toString();

        if ("APPROVED".equalsIgnoreCase(verificationStatus)) {
            userService.updateKycStatus(userId, true);
            return ResponseEntity.ok("KYC verification approved");
        } else if ("REJECTED".equalsIgnoreCase(verificationStatus)) {
            userService.updateKycStatus(userId, false);
            return ResponseEntity.ok("KYC verification rejected");
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid KYC status");
    }
}
