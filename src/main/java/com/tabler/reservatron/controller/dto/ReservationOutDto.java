package com.tabler.reservatron.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReservationOutDto {
    @JsonProperty("customer_name")
    private String customerName;
    private LocalDateTime from;
    private LocalDateTime to;
}
