package com.RentVAT.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class BookingResponseDTO {
    private Long id;
    private Long renterId;
    private String renterUsername;
    private String renterEmail;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private BigDecimal totalPrice;
    private BigDecimal rentalPrice;
    private BigDecimal platformCommission;
    private boolean kycVerified;
    private LocalDateTime createdAt;
}
