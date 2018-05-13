package com.tabler.reservatron.entity;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservations")
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Reservation {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(name = "reservation_id")
    private String reservationId;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "desk_id")
    @Setter
    private Desk desk;
    @Column
    private String guest;
    @Column(name = "time_from")
    private LocalDateTime timeFrom;
    @Column(name = "time_to")
    private LocalDateTime timeTo;
}
