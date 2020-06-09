package com.example.parallelcalls.controller;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.parallelcalls.entity.User;
import com.example.parallelcalls.service.GitHubLookupService;

@RestController
public class ExampleController {

    private static final Logger logger = LoggerFactory.getLogger(ExampleController.class);

    @Autowired
    private GitHubLookupService gitHubLookupService;

    @GetMapping("/resources")
    public ResponseEntity<String> get() throws InterruptedException, ExecutionException {

        // Start the clock
        long start = System.currentTimeMillis();

        // Kick of multiple, asynchronous lookups
        CompletableFuture<User> page1 = gitHubLookupService.findUser("PivotalSoftware");
        CompletableFuture<User> page2 = gitHubLookupService.findUser("CloudFoundry");
        CompletableFuture<User> page3 = gitHubLookupService.findUser("Spring-Projects");

        // Wait until they are all done
        CompletableFuture.allOf(page1, page2, page3).join();

        // Print results, including elapsed time
        logger.info("Elapsed time: " + (System.currentTimeMillis() - start));
        logger.info("--> " + page1.get());
        logger.info("--> " + page2.get());
        logger.info("--> " + page3.get());

        return ResponseEntity.ok("OK");
    }

}
