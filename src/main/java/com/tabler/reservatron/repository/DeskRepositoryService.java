package com.tabler.reservatron.repository;

import com.tabler.reservatron.entity.Desk;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class DeskRepositoryService {

    @Autowired
    private DeskRepository deskRepository;

    public Desk save(Desk desk) {
        return deskRepository.save(desk);
    }

    public Optional<Desk> findById(Long id) {
        return deskRepository.findById(id);
    }

    public Integer findCollisions(Desk table, LocalDateTime timeFrom, LocalDateTime timeTo) {
        return deskRepository.findCollisions(table.getDeskId(), Timestamp.valueOf(timeFrom), Timestamp.valueOf(timeTo));
    }
}
