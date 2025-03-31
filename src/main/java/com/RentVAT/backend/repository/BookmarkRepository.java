package com.RentVAT.backend.repository;

import com.RentVAT.backend.models.Bookmark;
import com.RentVAT.backend.models.Listing;
import com.RentVAT.backend.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    List<Bookmark> findByUserId(Long userId);
    boolean existsByUserAndListing(User user, Listing listing);
    Optional<Bookmark> findByUserAndListing(User user, Listing listing);

}
