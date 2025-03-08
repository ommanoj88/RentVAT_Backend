package com.RentVAT.backend.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookingRequestDTO {
    @NotNull(message = "Listing ID is required")
    private Long listingId;

    @NotNull(message = "Renter ID is required")
    private Long renterId;

    @FutureOrPresent(message = "Start date must be today or in the future")
    private String startDate;

    @FutureOrPresent(message = "End date must be today or in the future")
    private String endDate;

}
