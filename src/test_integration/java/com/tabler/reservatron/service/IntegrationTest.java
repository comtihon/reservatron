package com.tabler.reservatron.service;

import com.tabler.reservatron.controller.dto.ResponseDTO;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

public abstract class IntegrationTest {
    @LocalServerPort
    protected int port;

    @Autowired
    protected TestRestTemplate restTemplate;

    protected void performReservation(int code, String message, String reservation) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        ResponseEntity<ResponseDTO> response = restTemplate.exchange(
                "http://localhost:" + port + "/api/v1/table/" + 1 + "/reservation",
                HttpMethod.POST,
                new HttpEntity<>(reservation, headers),
                ResponseDTO.class);
        Assert.assertEquals(code, response.getStatusCodeValue());
        Assert.assertEquals(message, response.getBody().getResponse());
    }
}
