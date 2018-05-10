package com.tabler.reservatron.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TableWithReservationsDto {
    @JsonProperty("id")
    private Long tableId;
    @JsonProperty("name")
    private String tableName;
    private List<ReservationOutDto> reservations;
}
