package com.tabler.reservatron.graphql;

import com.tabler.reservatron.graphql.type.ReservationResult;
import com.tabler.reservatron.service.ReservationService;
import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLNonNull;
import graphql.annotations.annotationTypes.GraphQLRelayMutation;
import lombok.AllArgsConstructor;

import java.time.ZonedDateTime;

@AllArgsConstructor
@GraphQLName("Mutation")
public class MutationDto {
    private ReservationService reservationService;

    @GraphQLField
    @GraphQLRelayMutation
    public ReservationResult reserveTable(@GraphQLNonNull Long tableId,
                                          @GraphQLNonNull String guest,
                                          @GraphQLNonNull ZonedDateTime from,
                                          @GraphQLNonNull ZonedDateTime to) {
        return reservationService.makeReservation(tableId, guest, from, to);
    }
}
