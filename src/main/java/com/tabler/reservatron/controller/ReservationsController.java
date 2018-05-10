package com.tabler.reservatron.controller;

import com.tabler.reservatron.controller.dto.ReservationDto;
import com.tabler.reservatron.controller.dto.ResponseDTO;
import com.tabler.reservatron.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.concurrent.CompletableFuture;

@RestController
public class ReservationsController {

    @Autowired
    private ReservationService reservationService;

    @RequestMapping(path = "/api/v1/table/{id}/reservation", method = RequestMethod.POST)
    public CompletableFuture<ResponseEntity<?>> getTransaction(@PathVariable Long id, @Valid @RequestBody ReservationDto reservation) {
        CompletableFuture<ResponseDTO<?>> respond = reservationService.makeReservation(id, reservation);
        return respond.thenApply(r -> {
            if (r.isResult()) {
                return new ResponseEntity<>(r, HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>(r, HttpStatus.BAD_REQUEST);
            }
        });
    }

    @RequestMapping(path = "/api/v1/table/{id}", method = RequestMethod.GET)
    public CompletableFuture<ResponseEntity<?>> getStatistics(@PathVariable Long id) {
        CompletableFuture<ResponseDTO<?>> statistics = reservationService.getReservations(id);
        return statistics.thenApply(r -> {
            if(r.isResult()) {
                return new ResponseEntity<>(r.getResponse(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(r, HttpStatus.BAD_REQUEST);
            }
        });
    }
}
