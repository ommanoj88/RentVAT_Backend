package com.RentVAT.backend.controllers;

import com.RentVAT.backend.models.User;
import com.RentVAT.backend.repositories.UserRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    // Register new user
    @PostMapping("/register")
    public User registerUser(@RequestBody User user) throws FirebaseAuthException {
        UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                .setEmail(user.getEmail())
                .setPassword("defaultPassword")  // User can change later
                .setDisplayName(user.getName())
                .setPhoneNumber(user.getPhone());

        UserRecord userRecord = FirebaseAuth.getInstance().createUser(request);

        user.setFirebaseUid(userRecord.getUid());
        return userRepository.save(user);
    }

    // Get user by Firebase UID
    @GetMapping("/{firebaseUid}")
    public User getUser(@PathVariable String firebaseUid) {
        return userRepository.findByFirebaseUid(firebaseUid);
    }
}
