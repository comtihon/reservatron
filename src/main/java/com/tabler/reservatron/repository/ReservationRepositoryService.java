package com.tabler.reservatron.repository;

import com.tabler.reservatron.entity.Reservation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReservationRepositoryService {

    @Autowired
    private ReservationRepository reservationRepository;

    public Optional<Reservation> findById(String id) {
        return reservationRepository.findById(id);
    }

    public List<Reservation> findByTable(Long deskId) {
        return reservationRepository.findByTableId(deskId);
    }
}
