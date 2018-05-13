package com.tabler.reservatron.config;

import com.tabler.reservatron.graphql.MutationDto;
import com.tabler.reservatron.graphql.QueryDto;
import com.tabler.reservatron.graphql.SubscriptionDto;
import com.tabler.reservatron.graphql.subscription.SubscriptionHandler;
import com.tabler.reservatron.graphql.type.ZonedDateTimeTypeFunction;
import com.tabler.reservatron.service.ReservationService;
import graphql.GraphQL;
import graphql.annotations.processor.GraphQLAnnotations;
import graphql.annotations.strategies.EnhancedExecutionStrategy;
import graphql.schema.GraphQLSchema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import static graphql.schema.GraphQLSchema.newSchema;

@Configuration
@EnableWebSocket
public class GraphqlConfig implements WebSocketConfigurer {

    @Autowired
    private ReservationService reservationService;
    @Autowired
    private SubscriptionHandler subscriptionHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(subscriptionHandler, "/subscriptions");
    }

    @Bean
    public GraphQLSchema schema() {
        GraphQLAnnotations.register(new ZonedDateTimeTypeFunction());
        return newSchema()
                .query(GraphQLAnnotations.object(QueryDto.class))
                .mutation(GraphQLAnnotations.object(MutationDto.class))
                .subscription(GraphQLAnnotations.object(SubscriptionDto.class))
                .build();
    }

    @Bean
    public GraphQL graphQL(GraphQLSchema schema) {
        return GraphQL.newGraphQL(schema)
                .queryExecutionStrategy(new EnhancedExecutionStrategy())
                .build();
    }

    @Bean
    public MutationDto mutation() {
        return new MutationDto(reservationService);
    }
}
