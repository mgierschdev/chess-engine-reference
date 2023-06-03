package com.backend.controllers;

import com.backend.domain.ChessGame;
import com.backend.models.Greeting;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicLong;

@RestController
public class ChessController {
    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    private ChessGame chessGame;

    @GetMapping("/move")
    public Greeting greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
        return new Greeting(counter.incrementAndGet(), String.format(template, name));
    }


//    @GetMapping("/getBoard")
//    public Greeting greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
//        return new Greeting(counter.incrementAndGet(), String.format(template, name));
//    }
}
