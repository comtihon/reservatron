package com.tabler.reservatron.repository;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
public class DeskRepositoryTest {

    @Autowired
    private DeskRepository repository;

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:reservations.sql")
    public void testCollisions() {
//      collision: reservation.to and other reservation.from
        Assert.assertNotEquals(Integer.valueOf(0), repository.findCollisions(1L,
                getDate(13, 0),
                getDate(15, 30)));
//      collision: one reservation in another
        Assert.assertNotEquals(Integer.valueOf(0), repository.findCollisions(1L,
                getDate(14, 0),
                getDate(16, 30)));
//      collision: reservation.to and other reservation.from
        Assert.assertNotEquals(Integer.valueOf(0), repository.findCollisions(1L,
                getDate(14, 0),
                getDate(15, 30)));
//      no collisions, case with borders
        Assert.assertEquals(Integer.valueOf(0), repository.findCollisions(1L,
                getDate(16, 0),
                getDate(17, 0)));
//      collision: same reservation.from
        Assert.assertNotEquals(Integer.valueOf(0), repository.findCollisions(1L,
                getDate(15, 0),
                getDate(17, 0)));
//      collision: same time for two reservations
        Assert.assertNotEquals(Integer.valueOf(0), repository.findCollisions(1L,
                getDate(17, 0),
                getDate(20, 0)));
//      collision: one reservation in another
        Assert.assertNotEquals(Integer.valueOf(0), repository.findCollisions(1L,
                getDate(18, 0),
                getDate(19, 0)));
//      no collisions, no borders
        Assert.assertEquals(Integer.valueOf(0), repository.findCollisions(1L,
                getDate(21, 0),
                getDate(22, 0)));
//      collision with all reservations
        Assert.assertNotEquals(Integer.valueOf(0), repository.findCollisions(1L,
                getDate(12, 30),
                getDate(18, 30)));
    }

    private Date getDate(int h, int m) {
        return Timestamp.valueOf(LocalDateTime.of(2018, 5, 4, h, m, 0, 0));
    }
}