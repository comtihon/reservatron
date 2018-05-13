package com.tabler.reservatron.graphql.subscription;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class GraphqlSubscriber<T> implements Subscriber<T> {
    private final WebSocketSession session;

    @Override
    public void onSubscribe(Subscription s) {
//        TODO this is not called. Need to investigate why

    }

    @Override
    public void onNext(T t) {
        try {
            sendMessage(t);
        } catch (IOException e) {
            log.warn("Can't send message to socket: {}", e.getMessage());
        }
    }

    @Override
    public void onError(Throwable t) {

    }

    @Override
    public void onComplete() {
    }

    private void sendMessage(T result) throws IOException {
        String resultJson = new ObjectMapper().writeValueAsString(result);
        session.sendMessage(new TextMessage(resultJson));
    }
}
