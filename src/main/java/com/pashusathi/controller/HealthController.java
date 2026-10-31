package com.pashusathi.controller;

import java.time.Instant;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HealthController {

    @GetMapping("/health")
    public Map<String, Object> health() {
        return Map.of(
                "status", "UP",
                "app", "PashuSathi",
                "time", Instant.now().toString());
    }

    @GetMapping("/version")
    public Map<String, String> version() {
        return Map.of("version", "0.0.1");
    }
}