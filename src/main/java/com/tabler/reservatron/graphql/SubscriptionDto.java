package com.tabler.reservatron.graphql;

import com.tabler.reservatron.graphql.dto.ReservationDto;
import com.tabler.reservatron.graphql.subscription.RTReservationDataFetcher;
import graphql.annotations.annotationTypes.GraphQLDataFetcher;
import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLNonNull;

@GraphQLName("GraphQlSubscription")
public class SubscriptionDto {
    @GraphQLField
    @GraphQLDataFetcher(RTReservationDataFetcher.class)
    public ReservationDto newReservationMade(@GraphQLNonNull Long tableId) {
        return null;
    }
}
