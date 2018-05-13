package com.tabler.reservatron.service;

import com.tabler.reservatron.graphql.type.ReservationResult;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;

import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
public class MakeReservationTest extends IntegrationTest {
    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:table.sql")
    public void makeReservation() {
        ReservationResult.ReservationStatus reservationStatus = performReservation(1L,
                "Mr. Smith",
                "2018-01-04T18:00:00.000+0000",
                "2018-01-04T20:00:00.000+0000");
        Assert.assertEquals(ReservationResult.ReservationStatus.SUCCESS, reservationStatus);

        reservationStatus = performReservation(1L,
                "Mr. Smith",
                "2018-01-04T18:00:00.000+0000",
                "2018-01-04T20:00:00.000+0000");
        Assert.assertEquals(ReservationResult.ReservationStatus.CONFLICT, reservationStatus);

        reservationStatus = performReservation(1L,
                "John Doe",
                "2018-01-04T17:30:00.000+0000",
                "2018-01-04T18:00:00.000+0000");
        Assert.assertEquals(ReservationResult.ReservationStatus.SUCCESS, reservationStatus);

        List<Map> reservations = getReservations(1L);
        Assert.assertEquals(2, reservations.size());
        Assert.assertEquals("Mr. Smith", reservations.get(0).get("guest"));
        Assert.assertEquals("2018-01-04T18:00:00.000+0000", reservations.get(0).get("from"));
        Assert.assertEquals("2018-01-04T20:00:00.000+0000", reservations.get(0).get("to"));

        Assert.assertEquals("John Doe", reservations.get(1).get("guest"));
        Assert.assertEquals("2018-01-04T17:30:00.000+0000", reservations.get(1).get("from"));
        Assert.assertEquals("2018-01-04T18:00:00.000+0000", reservations.get(1).get("to"));
    }

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:table.sql")
    public void reserveDifferentTimezone() {
        ReservationResult.ReservationStatus reservationStatus = performReservation(1L,
                "Mr. Smith",
                "2018-01-04T18:00:00.000+0000",
                "2018-01-04T20:00:00.000+0000");
        Assert.assertEquals(ReservationResult.ReservationStatus.SUCCESS, reservationStatus);

        reservationStatus = performReservation(1L,
                "John Doe",
                "2018-01-04T18:00:00.000+0200",
                "2018-01-04T20:00:00.000+0200");
        Assert.assertEquals(ReservationResult.ReservationStatus.SUCCESS, reservationStatus);

        List<Map> reservations = getReservations(1L);
        Assert.assertEquals(2, reservations.size());
        Assert.assertEquals("Mr. Smith", reservations.get(0).get("guest"));
        Assert.assertEquals("2018-01-04T18:00:00.000+0000", reservations.get(0).get("from"));
        Assert.assertEquals("2018-01-04T20:00:00.000+0000", reservations.get(0).get("to"));

        Assert.assertEquals("John Doe", reservations.get(1).get("guest"));
        Assert.assertEquals("2018-01-04T16:00:00.000+0000", reservations.get(1).get("from"));
        Assert.assertEquals("2018-01-04T18:00:00.000+0000", reservations.get(1).get("to"));
    }
}
