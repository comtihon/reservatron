package com.tabler.reservatron.service;

import com.tabler.reservatron.entity.Desk;
import com.tabler.reservatron.entity.Reservation;
import com.tabler.reservatron.graphql.dto.ReservationDto;
import com.tabler.reservatron.graphql.type.ReservationResult;
import com.tabler.reservatron.repository.DeskRepositoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.locks.LockRegistry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.concurrent.locks.Lock;

import static com.tabler.reservatron.graphql.type.ReservationResult.ReservationStatus.*;

@Slf4j
@Service
public class ReservationService {

    @Autowired
    private DeskRepositoryService deskRepositoryService;
    @Autowired
    private LockRegistry lockRegistry;
    @Autowired
    private SubscriptionService subscriptionService;

    @Transactional
    public ReservationResult makeReservation(Long tableId, String guest, ZonedDateTime from, ZonedDateTime to) {
        log.debug("Make reservation for {} [{} - {}]", tableId, from, to);
        Optional<Desk> maybeDesk = deskRepositoryService.findById(tableId);
        if (!maybeDesk.isPresent()) {
            return new ReservationResult(NO_SUCH_TABLE);
        }
        LocalDateTime toLocal = to.withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();
        LocalDateTime fromLocal = from.withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();
        Lock tableLock = lockRegistry.obtain(tableId.toString());
        tableLock.lock();
        try {
            Desk desk = maybeDesk.get();
            Integer numberOfCollisions = deskRepositoryService.findCollisions(desk, fromLocal, toLocal);
            if (numberOfCollisions == 0) {
                Reservation reservation = Reservation.builder()
                        .guest(guest)
                        .timeFrom(fromLocal)
                        .timeTo(toLocal)
                        .build();
                desk.addReservation(reservation);
                deskRepositoryService.save(desk);
                subscriptionService.newReservation(tableId, new ReservationDto(reservation));
                return new ReservationResult(SUCCESS);
            } else {
                return new ReservationResult(CONFLICT);
            }
        } finally {
            tableLock.unlock();
        }
    }
}
