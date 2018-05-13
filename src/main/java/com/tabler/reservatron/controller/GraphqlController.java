package com.tabler.reservatron.controller;

import com.tabler.reservatron.service.GraphqlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
public class GraphqlController {

    @Autowired
    private GraphqlService graphqlService;

    @CrossOrigin
    @RequestMapping(path = "/graphql", method = RequestMethod.POST)
    public CompletableFuture<ResponseEntity<?>> getTransaction(@RequestBody String query) {
        CompletableFuture<?> respond = graphqlService.executeQuery(query);
        return respond.thenApply(r -> new ResponseEntity<>(r, HttpStatus.OK));
    }
}
