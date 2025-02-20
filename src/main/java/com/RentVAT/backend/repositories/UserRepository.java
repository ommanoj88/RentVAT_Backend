package com.RentVAT.backend.repositories;

import com.RentVAT.backend.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByFirebaseUid(String firebaseUid); // ✅ Add this method
}
