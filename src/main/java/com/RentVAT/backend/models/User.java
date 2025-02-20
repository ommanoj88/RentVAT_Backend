package com.RentVAT.backend.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "application_user") //
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String firebaseUid;  // Unique ID from Firebase

    private String name;
    private String email;
    private String phone;
    private String profileImage; // URL of profile picture

    @Enumerated(EnumType.STRING)
    private Role role;

    public String getEmail() {
        return email;
    }
    public String getName() {
            return email;
    }
    public String getPhone() {
            return email;
}
    public void setFirebaseUid(String firebaseUid) {
        this.firebaseUid = firebaseUid;
    }
}
