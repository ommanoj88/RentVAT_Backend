package com.RentVAT.backend.controllers;

import com.RentVAT.backend.dto.BookmarkDTO;
import com.RentVAT.backend.dto.ListingDTO;
import com.RentVAT.backend.service.BookmarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/bookmarks")
@CrossOrigin(origins = "http://localhost:3000")
public class BookmarksController {

    @Autowired
    private BookmarkService bookmarkService;

    @PostMapping("/add")
    public ResponseEntity<?> addBookmark(@RequestParam Long userId, @RequestBody BookmarkDTO bookmarkDTO) {
        bookmarkService.addBookmark(userId, bookmarkDTO);
        return ResponseEntity.ok(Collections.singletonMap("message", "Bookmark added successfully"));
    }

    @GetMapping("/list")
    public List<ListingDTO> getAllBookmarkedListings(@RequestParam Long userId) {
        return bookmarkService.getAllBookmarkedListings(userId);
    }
}
