package com.hrpayroll.interfaces.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Health check controller for testing the application.
 */
@RestController
@RequestMapping("/api")
public class HealthController {

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("timestamp", Instant.now());
        response.put("application", "Employee HR and Payroll Management - DDD");
        response.put("version", "1.0.0-SNAPSHOT");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> info() {
        Map<String, Object> response = new HashMap<>();
        response.put("name", "Employee HR and Payroll Management System");
        response.put("description", "Domain-Driven Design with Event-Driven Architecture");
        response.put("version", "1.0.0-SNAPSHOT");
        response.put("java.version", System.getProperty("java.version"));
        response.put("spring.version", org.springframework.boot.SpringBootVersion.getVersion());
        return ResponseEntity.ok(response);
    }
}
