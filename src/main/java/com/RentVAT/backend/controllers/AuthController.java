package com.RentVAT.backend.controllers;

import com.RentVAT.backend.models.User;
import com.RentVAT.backend.service.UserService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User user) {
        try {
            // Check if email already exists in the database
            Optional<User> existingUser = userService.getUserByEmail(user.getEmail());
            if (existingUser.isPresent()) {
                return ResponseEntity.badRequest().body("Email already exists!");
            }

            // Create user in Firebase Authentication (Firebase handles password)
            UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                    .setEmail(user.getEmail());

            UserRecord userRecord = FirebaseAuth.getInstance().createUser(request);

            // Save user details in PostgreSQL (WITHOUT password)
            User newUser = User.builder()
                    .uid(userRecord.getUid()) // Store Firebase UID
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .build();

            userService.saveUser(newUser);
            return ResponseEntity.ok("User registered successfully with UID: " + userRecord.getUid());

        } catch (FirebaseAuthException e) {
            return ResponseEntity.badRequest().body("Registration failed: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Map<String, String> request) {
        try {
            String token = request.get("token");
            if (token == null || token.isEmpty()) {
                return ResponseEntity.badRequest().body("Token is missing in the request");
            }

            // Verify Firebase Token
            String uid = FirebaseAuth.getInstance().verifyIdToken(token).getUid();
            return ResponseEntity.ok("Authenticated user ID: " + uid);
        } catch (FirebaseAuthException e) {
            return ResponseEntity.status(401).body("Invalid token: " + e.getMessage());
        }
    }
}

