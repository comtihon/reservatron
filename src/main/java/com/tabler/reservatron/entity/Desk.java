package com.tabler.reservatron.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity(name = "desks")
@Getter
@NoArgsConstructor
public class Desk {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "desk_id")
    private Long deskId;
    @Column
    private String name;
    @OneToMany(mappedBy = "desk", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Reservation> reservations = new HashSet<>();

    public void addReservation(Reservation reservation) {
        reservation.setDesk(this);
        reservations.add(reservation);
    }
}
