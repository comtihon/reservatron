package com.tabler.reservatron.service;

import com.tabler.reservatron.graphql.type.ReservationResult;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.integration.support.locks.LockRegistry;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;

import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
public class ConcurrentReservationTest extends IntegrationTest {
    @Autowired
    private LockRegistry lockRegistry;

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:table.sql")
    public void testWaitForLock() throws InterruptedException {
        ReservationResult.ReservationStatus reservationStatus = performReservation(1L,
                "Mr. Smith",
                "2018-01-04T18:00:00.000+0000",
                "2018-01-04T20:00:00.000+0000");
        Assert.assertEquals(ReservationResult.ReservationStatus.SUCCESS, reservationStatus);

        Lock lock = lockRegistry.obtain("1");
        Assert.assertTrue(lock.tryLock());  // lock is free
        Thread thread = new Thread(() -> {
            ReservationResult.ReservationStatus res = performReservation(1L,
                    "John Doe",
                    "2018-01-04T20:00:00.000+0000",
                    "2018-01-04T22:00:00.000+0000");
            Assert.assertEquals(ReservationResult.ReservationStatus.SUCCESS, res);
        });
        thread.start(); // another user tries to book this table
        Thread.sleep(1000);
        List<Map> reservations = getReservations(1L);
//        only one booking from Mr.Smith for this table for the last 1 second
        Assert.assertEquals(1, reservations.size());
        Assert.assertEquals("Mr. Smith", reservations.get(0).get("guest"));
        lock.unlock();
        thread.join();
//        second user successfully booked a table after we release the lock
        reservations = getReservations(1L);
        Assert.assertEquals(2, reservations.size());
        Assert.assertEquals("Mr. Smith", reservations.get(0).get("guest"));
        Assert.assertEquals("John Doe", reservations.get(1).get("guest"));

    }
}
