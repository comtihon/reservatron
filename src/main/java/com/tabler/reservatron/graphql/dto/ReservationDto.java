package com.tabler.reservatron.graphql.dto;

import com.tabler.reservatron.entity.Reservation;
import com.tabler.reservatron.graphql.type.AbstractId;
import com.tabler.reservatron.graphql.type.Node;
import com.tabler.reservatron.repository.ReservationRepositoryService;
import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLNonNull;
import graphql.schema.DataFetchingEnvironment;
import lombok.Getter;
import org.springframework.context.ApplicationContext;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@Getter
@GraphQLName("Reservation")
public class ReservationDto extends AbstractId implements Node {
    @GraphQLNonNull
    @GraphQLField
    private String guest;
    @GraphQLNonNull
    @GraphQLField
    private ZonedDateTime from;
    @GraphQLField
    @GraphQLNonNull
    private ZonedDateTime to;

    public ReservationDto(Reservation reservation) {
        super(ReservationDto.class, reservation.getReservationId());
        this.guest = reservation.getGuest();
        this.from = reservation.getTimeFrom().atZone(ZoneId.of("UTC"));
        this.to = reservation.getTimeTo().atZone(ZoneId.of("UTC"));
    }

    public static ReservationDto getById(DataFetchingEnvironment environment, String id) {
        ApplicationContext context = environment.getContext();
        ReservationRepositoryService repositoryService = context.getBean(ReservationRepositoryService.class);
        return repositoryService.findById(id).map(ReservationDto::new).orElse(null);
    }
}
