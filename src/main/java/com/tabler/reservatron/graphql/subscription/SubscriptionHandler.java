package com.tabler.reservatron.graphql.subscription;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tabler.reservatron.service.GraphqlService;
import com.tabler.reservatron.service.SubscriptionService;
import graphql.ExecutionResult;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class SubscriptionHandler extends TextWebSocketHandler {
    @Autowired
    private SubscriptionService service;
    @Autowired
    private GraphqlService graphqlService;

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        CompletableFuture<ExecutionResult> result = graphqlService.executeQuery(message.getPayload());
        result.thenAccept(r ->
        {
            if (r.getData() instanceof Publisher) {
                handleSubscription(session, r);
            } else {
                try {
                    sendMessage(session, r);
                } catch (IOException e) {
                    log.warn("Can't send message to socket: {}", e.getMessage());
                }
            }
        });
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        service.unsubscribe(session.getId());
    }

    private void handleSubscription(WebSocketSession session, ExecutionResult result) {
        Publisher<ExecutionResult> stream = result.getData();
        Subscriber<ExecutionResult> subscriber = new GraphqlSubscriber<>(session);
        stream.subscribe(subscriber);
    }

    public static void sendMessage(WebSocketSession session, ExecutionResult result) throws IOException {
        String resultJson = new ObjectMapper().writeValueAsString(result);
        session.sendMessage(new TextMessage(resultJson));
    }
}
