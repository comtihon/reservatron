package com.tabler.reservatron.service;

import com.tabler.reservatron.controller.dto.ReservationOutDto;
import com.tabler.reservatron.controller.dto.TableWithReservationsDto;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.integration.support.locks.LockRegistry;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Comparator;
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
        performReservation(201, "OK", "{\n" +
                "  \"customer_name\": \"Mr. Smith\",\n" +
                "  \"timeslot\": {\n" +
                "    \"from\": \"2018-01-04T18:00:00.000+00:00\",\n" +
                "    \"to\": \"2018-01-04T20:00:00.000+00:00\"\n" +
                "  } \n" +
                "}\n");
        Lock lock = lockRegistry.obtain("1");
        Assert.assertTrue(lock.tryLock());  // lock is free
        Thread thread = new Thread(() -> performReservation(201, "OK", "{\n" +
                "  \"customer_name\": \"John Doe\",\n" +
                "  \"timeslot\": {\n" +
                "    \"from\": \"2018-01-04T20:00:00.000+00:00\",\n" +
                "    \"to\": \"2018-01-04T22:00:00.000+00:00\"\n" +
                "  } \n" +
                "}\n"));
        thread.start(); // another user tries to book this table
        Thread.sleep(1000);
        TableWithReservationsDto reservations =
                this.restTemplate.getForObject(
                        "http://localhost:" + port + "/api/v1/table/1", TableWithReservationsDto.class);
//        only one booking from Mr.Smith for this table for the last 1 second
        Assert.assertEquals(1, reservations.getReservations().size());
        Assert.assertEquals("Mr. Smith", reservations.getReservations().get(0).getCustomerName());
        lock.unlock();
        thread.join();
//        second user successfully booked a table after we release the lock
        reservations =
                this.restTemplate.getForObject(
                        "http://localhost:" + port + "/api/v1/table/1", TableWithReservationsDto.class);
        Assert.assertEquals(2, reservations.getReservations().size());
        reservations.getReservations().sort(Comparator.comparing(ReservationOutDto::getFrom));
        Assert.assertEquals("Mr. Smith", reservations.getReservations().get(0).getCustomerName());
        Assert.assertEquals("John Doe", reservations.getReservations().get(1).getCustomerName());
    }
}
