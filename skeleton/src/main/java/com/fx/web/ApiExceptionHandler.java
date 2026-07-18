package com.fx.web;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

/**
 * Global error handling — a customer never sees a Java stack trace.
 * This is the BASELINE: bad input -> 400 with a clean JSON message; anything unexpected
 * -> 500 with a generic message (no internals leaked).
 *
 * When you build the "validation & error handling" requirement, EXTEND this class:
 * add a 404 for an unknown currency pair, field-level validation messages, etc.
 */
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> badRequest(IllegalArgumentException ex) {
        return Map.of("error", ex.getMessage() == null ? "bad request" : ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> fallback(Exception ex) {
        // Deliberately generic — no stack trace, no internal detail to the browser.
        return Map.of("error", "Something went wrong. Please try again.");
    }
}
