package com.tabler.reservatron.service;

import com.tabler.reservatron.controller.dto.ReservationOutDto;
import com.tabler.reservatron.controller.dto.TableWithReservationsDto;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.Comparator;

import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
public class MakeReservationTest extends IntegrationTest {
    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:table.sql")
    public void makeReservation() {
        performReservation(201, "OK", "{\n" +
                "  \"customer_name\": \"Mr. Smith\",\n" +
                "  \"timeslot\": {\n" +
                "    \"from\": \"2018-01-04T18:00:00.000+00:00\",\n" +
                "    \"to\": \"2018-01-04T20:00:00.000+00:00\"\n" +
                "  } \n" +
                "}\n");

        performReservation(400, "CONFLICT", "{\n" +
                "  \"customer_name\": \"Mr. Smith\",\n" +
                "  \"timeslot\": {\n" +
                "    \"from\": \"2018-01-04T18:00:00.000+00:00\",\n" +
                "    \"to\": \"2018-01-04T20:00:00.000+00:00\"\n" +
                "  } \n" +
                "}\n");
        performReservation(201, "OK", "{\n" +
                "  \"customer_name\": \"John Doe\",\n" +
                "  \"timeslot\": {\n" +
                "    \"from\": \"2018-01-04T17:30:00.000+00:00\",\n" +
                "    \"to\": \"2018-01-04T18:00:00.000+00:00\"\n" +
                "  } \n" +
                "}\n");
        TableWithReservationsDto reservations =
                this.restTemplate.getForObject(
                        "http://localhost:" + port + "/api/v1/table/1", TableWithReservationsDto.class);
        Assert.assertEquals(2, reservations.getReservations().size());
        Assert.assertEquals(Long.valueOf(1), reservations.getTableId());
        Assert.assertEquals("test table", reservations.getTableName());
        reservations.getReservations().sort(Comparator.comparing(ReservationOutDto::getFrom));
        Assert.assertEquals("John Doe", reservations.getReservations().get(0).getCustomerName());
        Assert.assertEquals(LocalDateTime.parse("2018-01-04T17:30:00"), reservations.getReservations().get(0).getFrom());
        Assert.assertEquals(LocalDateTime.parse("2018-01-04T18:00:00"), reservations.getReservations().get(0).getTo());

        Assert.assertEquals("Mr. Smith", reservations.getReservations().get(1).getCustomerName());
        Assert.assertEquals(LocalDateTime.parse("2018-01-04T18:00:00"), reservations.getReservations().get(1).getFrom());
        Assert.assertEquals(LocalDateTime.parse("2018-01-04T20:00:00"), reservations.getReservations().get(1).getTo());
    }

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:table.sql")
    public void reserveDifferentTimezone() {
        performReservation(201, "OK", "{\n" +
                "  \"customer_name\": \"Mr. Smith\",\n" +
                "  \"timeslot\": {\n" +
                "    \"from\": \"2018-01-04T18:00:00.000+00:00\",\n" +
                "    \"to\": \"2018-01-04T20:00:00.000+00:00\"\n" +
                "  } \n" +
                "}\n");
        performReservation(201, "OK", "{\n" +
                "  \"customer_name\": \"John Doe\",\n" +
                "  \"timeslot\": {\n" +
                "    \"from\": \"2018-01-04T18:00:00.000+02:00\",\n" +
                "    \"to\": \"2018-01-04T20:00:00.000+02:00\"\n" +
                "  } \n" +
                "}\n");
        TableWithReservationsDto reservations =
                this.restTemplate.getForObject(
                        "http://localhost:" + port + "/api/v1/table/1", TableWithReservationsDto.class);
        Assert.assertEquals(2, reservations.getReservations().size());
        Assert.assertEquals(Long.valueOf(1), reservations.getTableId());
        Assert.assertEquals("test table", reservations.getTableName());
        reservations.getReservations().sort(Comparator.comparing(ReservationOutDto::getFrom));
        Assert.assertEquals("John Doe", reservations.getReservations().get(0).getCustomerName());
        Assert.assertEquals(LocalDateTime.parse("2018-01-04T16:00:00"), reservations.getReservations().get(0).getFrom());
        Assert.assertEquals(LocalDateTime.parse("2018-01-04T18:00:00"), reservations.getReservations().get(0).getTo());

        Assert.assertEquals("Mr. Smith", reservations.getReservations().get(1).getCustomerName());
        Assert.assertEquals(LocalDateTime.parse("2018-01-04T18:00:00"), reservations.getReservations().get(1).getFrom());
        Assert.assertEquals(LocalDateTime.parse("2018-01-04T20:00:00"), reservations.getReservations().get(1).getTo());
    }
}
