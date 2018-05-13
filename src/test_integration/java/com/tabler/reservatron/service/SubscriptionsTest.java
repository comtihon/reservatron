package com.tabler.reservatron.service;

import com.tabler.reservatron.graphql.type.ReservationResult;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketConnectionManager;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import static java.util.concurrent.TimeUnit.SECONDS;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SubscriptionsTest extends IntegrationTest {
    private final static String SUBSCRIBE_TO_TABLE = " subscription RealTimeReservationsSubscription {\n" +
            "   newReservationMade(tableId: %d) {\n" +
            "     guest\n" +
            "     from\n" +
            "     to\n" +
            "   }\n" +
            " }";

    private BlockingQueue<String> blockingQueue;
    private WebSocketConnectionManager connectionManager;

    @Before
    public void setup() {
        blockingQueue = new LinkedBlockingDeque<>();
        connectionManager = new WebSocketConnectionManager(new StandardWebSocketClient(),
                new TextWebSocketHandler() {
                    @Override
                    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
                        session.sendMessage(new TextMessage(String.format(SUBSCRIBE_TO_TABLE, 1)));
                    }

                    @Override
                    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
                        blockingQueue.add(message.getPayload());
                    }
                },
                "ws://localhost:" + port + "/subscriptions");
    }

    @After
    public void tearDown() throws Exception {
        connectionManager.stopInternal();
    }

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:table.sql")
    public void realTimeReservationSubscription() throws Exception {
        connectionManager.startInternal();
        Thread.sleep(500);
        ReservationResult.ReservationStatus reservationStatus = performReservation(1L,
                "Mr. Smith",
                "2018-01-05T18:00:00.000+0000",
                "2018-01-05T20:00:00.000+0000");
        Assert.assertEquals(ReservationResult.ReservationStatus.SUCCESS, reservationStatus);
        String message = blockingQueue.poll(1, SECONDS);
        Assert.assertNotNull(message);
        Assert.assertEquals("{\"data\":{\"newReservationMade\":" +
                "{\"guest\":\"Mr. Smith\"," +
                "\"from\":\"2018-01-05T18:00:00.000+0000\"," +
                "\"to\":\"2018-01-05T20:00:00.000+0000\"}}," +
                "\"errors\":[],\"extensions\":null}", message);
    }

}
