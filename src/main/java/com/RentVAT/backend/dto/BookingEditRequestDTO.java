package com.RentVAT.backend.dto;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class BookingEditRequestDTO {
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal totalPrice;
}
