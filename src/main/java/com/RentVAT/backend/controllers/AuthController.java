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
    public ResponseEntity<String> register(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            String password = request.get("password");
            String username = request.get("username");

            if (email == null || password == null || username == null) {
                return ResponseEntity.badRequest().body("Email, password, and username are required");
            }

            // Check if email already exists
            Optional<User> existingUser = userService.getUserByEmail(email);
            if (existingUser.isPresent()) {
                return ResponseEntity.badRequest().body("Email already exists!");
            }

            // Create user in Firebase Authentication
            UserRecord.CreateRequest firebaseRequest = new UserRecord.CreateRequest()
                    .setEmail(email)
                    .setPassword(password); // ✅ Set password here!

            UserRecord userRecord = FirebaseAuth.getInstance().createUser(firebaseRequest);

            // Save user details in PostgreSQL (excluding password for security reasons)
            User newUser = User.builder()
                    .uid(userRecord.getUid()) // Store Firebase UID
                    .username(username)
                    .email(email)
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

