package com.RentVAT.backend.controllers;

import com.RentVAT.backend.models.User;
import com.RentVAT.backend.service.FirebaseAuthService;
import com.RentVAT.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private FirebaseAuthService firebaseAuthService;

    // Login endpoint
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Map<String, String> request) {
        try {
            String token = request.get("token"); // Extract token from JSON body
            if (token == null || token.isEmpty()) {
                return ResponseEntity.badRequest().body("Token is missing in the request");
            }

            String uid = firebaseAuthService.verifyToken(token);
            return ResponseEntity.ok("Authenticated user ID: " + uid);
        } catch (FirebaseAuthException e) {
            return ResponseEntity.status(401).body("Invalid token: " + e.getMessage());
        }
    }


    // Registration endpoint
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User user) {
        try {
            // Create a user in Firebase
            UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                    .setEmail(user.getEmail())
                    .setPassword(user.getPassword());

            UserRecord userRecord = FirebaseAuth.getInstance().createUser(request);

            // Save user details in the database
            user.setUid(userRecord.getUid()); // Set Firebase UID
            userService.saveUser(user); // Store user details in the local database

            return ResponseEntity.ok("User registered successfully with UID: " + userRecord.getUid());
        } catch (FirebaseAuthException e) {
            return ResponseEntity.status(400).body("Registration failed: " + e.getMessage());
        }
    }

}

