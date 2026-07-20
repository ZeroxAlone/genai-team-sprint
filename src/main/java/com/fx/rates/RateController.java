package com.fx.rates;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Rate controller — REST endpoint for fetching the latest exchange rates.
 * GET /api/rates -> 200 + JSON array of Rate objects.
 *
 * AC1: returns JSON array with {base, quote, rate, rateDate}.
 * AC2: exactly one row per pair (latest rateDate).
 * AC5: empty DB -> [] with HTTP 200 (no 500).
 */
@RestController
public class RateController {

    private final RateRepository repo;

    public RateController(RateRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/api/rates")
    public List<Rate> all() {
        return repo.findLatestRates();
    }
}
