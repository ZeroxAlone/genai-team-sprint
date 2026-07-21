package com.fx.rates;

/**
 * Thrown when a specific currency pair has no rate on record.
 * Caught by com.fx.web.ApiExceptionHandler -> clean 404 JSON (no stack trace).
 */
public class RateNotFoundException extends RuntimeException {

    public RateNotFoundException(String base, String quote) {
        super("no rate found for " + base + "/" + quote);
    }
}
