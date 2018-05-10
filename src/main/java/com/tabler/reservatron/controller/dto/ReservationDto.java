package com.tabler.reservatron.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ReservationDto {
    @NotNull
    @JsonProperty("customer_name")
    private String customerName;
    @NotNull
    private Timeslot timeslot;

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    public static class Timeslot {
        @NotNull
        private ZonedDateTime from;
        @NotNull
        private ZonedDateTime to;
    }
}
