package com.tabler.reservatron.service;

import com.tabler.reservatron.graphql.dto.ReservationDto;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SubscriptionService {

    private final Map<Long, Publisher<ReservationDto>> tablePublishers = new ConcurrentHashMap<>();
    private final Map<String, Long> subscriptionToTableMap = new ConcurrentHashMap<>();
    private final Map<Long, Map<String, Subscriber<? super ReservationDto>>> subscriptionsByTable = new ConcurrentHashMap<>();

    public Publisher<ReservationDto> getTableAwarePublisher(Long tableId) {
        return tablePublishers.computeIfAbsent(tableId, id -> s -> subscribeToReservations(s, tableId));
    }

    public void subscribeToReservations(Subscriber<? super ReservationDto> subscription, Long tableId) {
        subscriptionsByTable
                .computeIfAbsent(tableId, id -> new ConcurrentHashMap<>())
                .put("sessionId", subscription);  // TODO
        subscriptionToTableMap.put("sessionId", tableId);
    }

    public void newReservation(Long tableId, ReservationDto reservationDto) {
        Map<String, Subscriber<? super ReservationDto>> subscriptions = subscriptionsByTable.get(tableId);
        if (subscriptions != null) {
            for (Map.Entry<String, Subscriber<? super ReservationDto>> entry : subscriptions.entrySet()) {
                entry.getValue().onNext(reservationDto);
            }
        }
    }

    public void unsubscribe(String sessionId) {
        Long tableId = subscriptionToTableMap.remove(sessionId);
        if (tableId != null) {
            Map<String, Subscriber<? super ReservationDto>> subscriptions = subscriptionsByTable.get(tableId);
            if (subscriptions != null) {
                subscriptions.remove(sessionId);
            }
        }
    }
}
