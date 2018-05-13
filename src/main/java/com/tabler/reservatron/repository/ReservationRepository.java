package com.tabler.reservatron.repository;

import com.tabler.reservatron.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, String> {

    @Query("select r from Reservation r where desk.deskId = :deck_id")
    List<Reservation> findByTableId(@Param("deck_id") Long deskId);


}

