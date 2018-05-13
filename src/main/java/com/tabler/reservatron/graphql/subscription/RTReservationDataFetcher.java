package com.tabler.reservatron.graphql.subscription;

import com.tabler.reservatron.service.SubscriptionService;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.context.ApplicationContext;

public class RTReservationDataFetcher implements DataFetcher {
    @Override
    public Object get(DataFetchingEnvironment environment) {
        ApplicationContext context = environment.getContext();
        SubscriptionService service = context.getBean(SubscriptionService.class);
        return service.getTableAwarePublisher(environment.getArgument("tableId"));
    }
}
