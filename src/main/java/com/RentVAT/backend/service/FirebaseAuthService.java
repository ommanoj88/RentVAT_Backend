package com.RentVAT.backend.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import org.springframework.stereotype.Service;

@Service
public class FirebaseAuthService {

    public String verifyToken(String token) throws FirebaseAuthException {
        return FirebaseAuth.getInstance().verifyIdToken(token).getUid();
    }
}
