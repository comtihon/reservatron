package com.tabler.reservatron.service;

import com.tabler.reservatron.graphql.type.ReservationResult;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class IntegrationTest {

    protected static final String RESERVE_MUTATION = "mutation Reserve\n" +
            "{reserveTable(input: {tableId: %d, " +
            "                      guest: \"%s\", " +
            "                      from: \"%s\", " +
            "                      to: \"%s\", " +
            "                      clientMutationId: \"uuid\"}) " +
            "{status}}";
    protected static final String GET_TABLES_QUERY = "query \n" +
            "{allTables " +
            "   {edges {node {id name tableId " +
            "                }" +
            "           }" +
            "   }" +
            "}";
    protected static final String GET_TABLE_RESERVATIONS = "query \n" +
            "{node(id: \"%s\") \n" +
            "{... on Table { reservations {edges {node {guest from to}} }}}}";
    protected static final String GET_ALL_QUERY = "query \n" +
            "{allTables " +
            "   {edges {node {name reservations " +
            "                           {edges {node {guest from to}}}" +
            "                }" +
            "           }" +
            "   }" +
            "}";

    @LocalServerPort
    protected int port;

    @Autowired
    protected TestRestTemplate restTemplate;

    protected ReservationResult.ReservationStatus performReservation(Long tableId,
                                                                     String guest,
                                                                     String from,
                                                                     String to) {
        Map response = request(String.format(RESERVE_MUTATION, tableId, guest, from, to));
        Assert.assertTrue(((List) response.get("errors")).isEmpty());
        return ReservationResult.ReservationStatus.valueOf((String) ((Map)((Map) response.get("data")).get("reserveTable")).get("status"));
    }

    protected List<Map> getReservations(Long tableId) {
        Map table = getTable(tableId);
        Assert.assertNotNull(table);
        Map response = request(String.format(GET_TABLE_RESERVATIONS, table.get("id")));
        Assert.assertTrue(((List) response.get("errors")).isEmpty());
        Map<String, Object> data = (Map<String, Object>) response.get("data");
        return ((List<Map>) ((Map) ((Map) data.get("node")).get("reservations")).get("edges")).stream()
                .map(node -> (Map) node.get("node"))
                .collect(Collectors.toList());
    }

    protected Map getTable(Long tableId) {
        Map response = request(GET_TABLES_QUERY);
        Assert.assertTrue(((List) response.get("errors")).isEmpty());
        Map<String, Object> data = (Map<String, Object>) response.get("data");
        return ((List<Map>) ((Map) data.get("allTables")).get("edges")).stream()
                .map(node -> (Map) node.get("node"))
                .filter(node -> Long.valueOf((Integer) node.get("tableId")).equals(tableId))
                .findFirst().orElse(null);
    }

    protected Map request(String body) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        ResponseEntity<Map> response = restTemplate.exchange(
                "http://localhost:" + port + "/graphql",
                HttpMethod.POST,
                new HttpEntity<>(body, headers),
                Map.class);
        Assert.assertEquals(200, response.getStatusCodeValue());
        return response.getBody();
    }
}
