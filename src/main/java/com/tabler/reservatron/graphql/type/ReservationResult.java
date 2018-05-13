package com.tabler.reservatron.graphql.type;

import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLNonNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class ReservationResult {
    @Getter
    @GraphQLNonNull
    @GraphQLField
    private ReservationStatus status;

    public enum ReservationStatus {
        CONFLICT,
        NO_SUCH_TABLE,
        SUCCESS
    }
}
