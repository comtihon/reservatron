package com.tabler.reservatron.repository;

import com.tabler.reservatron.entity.Desk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface DeskRepository extends JpaRepository<Desk, Long> {

    @Query(value = "select count(*) from reservations as r join desks as d on d.desk_id=r.desk_id where r.desk_id=:deck_id AND (" +
            "(" +
            "   (time_from between :time_from AND :time_to) " +
            "OR (time_to between :time_from AND :time_to) " +
            "OR (:time_from between time_from and time_to) " +
            "OR (:time_to between time_from and time_to)" +
            ") " +
            "AND (time_to!=:time_from AND time_from!=:time_to))", nativeQuery = true)
    Integer findCollisions(@Param("deck_id") Long deckId,
                           @Param("time_from") Date timeFrom,
                           @Param("time_to") Date timeTo);
}

