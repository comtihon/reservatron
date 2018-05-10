package com.tabler.reservatron.service;

import com.tabler.reservatron.controller.dto.ReservationDto;
import com.tabler.reservatron.controller.dto.ReservationOutDto;
import com.tabler.reservatron.controller.dto.ResponseDTO;
import com.tabler.reservatron.controller.dto.TableWithReservationsDto;
import com.tabler.reservatron.entity.Desk;
import com.tabler.reservatron.entity.Reservation;
import com.tabler.reservatron.repository.DeskRepositoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.locks.LockRegistry;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ReservationService {

    @Autowired
    private DeskRepositoryService deskRepositoryService;
    @Autowired
    private LockRegistry lockRegistry;

    @Async
    @Transactional
    public CompletableFuture<ResponseDTO<?>> makeReservation(Long tableId, ReservationDto reservation) {
        log.debug("Make reservation for {} {}", tableId, reservation);
        Optional<Desk> maybeDesk = deskRepositoryService.findById(tableId);
        if (!maybeDesk.isPresent()) {
            return CompletableFuture.completedFuture(ResponseDTO.noSuchTable());
        }
        LocalDateTime to = reservation.getTimeslot().getTo().withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();
        LocalDateTime from = reservation.getTimeslot().getFrom().withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();
        Lock tableLock = lockRegistry.obtain(tableId.toString());
        tableLock.lock();
        try {
            Desk desk = maybeDesk.get();
            Integer numberOfCollisions = deskRepositoryService.findCollisions(desk, from, to);
            if (numberOfCollisions == 0) {
                desk.addReservation(Reservation.builder()
                        .guest(reservation.getCustomerName())
                        .timeFrom(from)
                        .timeTo(to)
                        .build());
                deskRepositoryService.save(desk);
                return CompletableFuture.completedFuture(new ResponseDTO<>(true, "OK"));
            } else {
                return CompletableFuture.completedFuture(ResponseDTO.reservationConflict());
            }
        } finally {
            tableLock.unlock();
        }
    }

    @Async
    @Transactional
    public CompletableFuture<ResponseDTO<?>> getReservations(Long tableId) {
        log.debug("Get reservations for {}", tableId);
        Optional<Desk> maybeDesk = deskRepositoryService.findById(tableId);
        if (!maybeDesk.isPresent()) {
            return CompletableFuture.completedFuture(ResponseDTO.noSuchTable());
        }
        Desk desk = maybeDesk.get();
        TableWithReservationsDto result =
                new TableWithReservationsDto(desk.getDeskId(), desk.getName(),
                        desk.getReservations().stream()
                                .map(r -> new ReservationOutDto(r.getGuest(), r.getTimeFrom(), r.getTimeTo()))
                                .collect(Collectors.toList()));
        return CompletableFuture.completedFuture(new ResponseDTO<>(true, result));
    }

}
