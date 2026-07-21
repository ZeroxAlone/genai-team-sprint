package com.fx.rates;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Rate controller — REST endpoints for fetching exchange rates.
 * GET /api/rates -> 200 + JSON array of Rate objects.
 * GET /api/rates/{base}/{quote} -> 200 + single Rate, or 404 if the pair is unknown.
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

    @GetMapping("/api/rates/{base}/{quote}")
    public Rate one(@PathVariable String base, @PathVariable String quote) {
        String b = base.toUpperCase();
        String q = quote.toUpperCase();
        return repo.findRate(b, q).orElseThrow(() -> new RateNotFoundException(b, q));
    }

    @GetMapping("/api/rates/{base}/{quote}/history")
    public List<Rate> history(@PathVariable String base, @PathVariable String quote) {
        String b = base.toUpperCase();
        String q = quote.toUpperCase();
        return repo.findRateHistory(b, q);
    }
}

