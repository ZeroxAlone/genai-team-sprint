package com.fx.rates;

public class RateNotFoundException extends RuntimeException {

    public RateNotFoundException(String from, String to) {
        super("Rate not found for " + from + " to " + to);
    }
}