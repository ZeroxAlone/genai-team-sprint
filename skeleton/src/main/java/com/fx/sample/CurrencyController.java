package com.fx.sample;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * SAMPLE feature — the REST endpoint the Currencies UI page calls.
 * GET /api/currencies  ->  200 + JSON array of every supported currency.
 * Thin controller: it delegates to the repository and returns the result as JSON.
 */
@RestController
public class CurrencyController {

    private final CurrencyRepository repo;

    public CurrencyController(CurrencyRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/api/currencies")
    public List<Currency> all() {
        return repo.findAll();
    }
}
