package com.tabler.reservatron.repository;

import com.tabler.reservatron.entity.Desk;
import com.tabler.reservatron.graphql.type.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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

    public List<Desk> findAll() {
        return deskRepository.findAll();
    }

    public List<Desk> findPaginated(Page page) {
        String id = page.getId();
        if(id == null) {
            if(page.getLast() != null) {
//                TODO
            } else {
//                TODO
            }
        } else {
            if(page.getLast() != null) {
//                TODO
            } else {
//                TODO
            }
        }
        return new ArrayList<>();
    }
}
