package com.fx.rates;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Rate controller web-slice tests. Mock the repository so no database is needed.
 * AC1: returns JSON array with {base, quote, rate, rateDate}.
 * AC3: EUR/USD shows rate=1.0818, rateDate="2026-01-12".
 * AC5: empty DB returns [] with HTTP 200 (no 500).
 */
@WebMvcTest(RateController.class)
class RateControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    RateRepository repo;

    @Test
    void returnsRatesAsJsonArray() throws Exception {
        when(repo.findLatestRates()).thenReturn(List.of(
                new Rate("EUR", "USD", "1.0818", "2026-01-12"),
                new Rate("USD", "JPY", "147.6026", "2026-01-12")));

        mvc.perform(get("/api/rates"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].base").value("EUR"))
                .andExpect(jsonPath("$[0].quote").value("USD"))
                .andExpect(jsonPath("$[0].rate").value("1.0818"))
                .andExpect(jsonPath("$[0].rateDate").value("2026-01-12"));
    }

    @Test
    void returnsEurUsdCheckpoint() throws Exception {
        // AC3: EUR/USD reads rate = 1.0818, rateDate = "2026-01-12"
        when(repo.findLatestRates()).thenReturn(List.of(
                new Rate("EUR", "USD", "1.0818", "2026-01-12")));

        mvc.perform(get("/api/rates"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].base").value("EUR"))
                .andExpect(jsonPath("$[0].quote").value("USD"))
                .andExpect(jsonPath("$[0].rate").value("1.0818"))
                .andExpect(jsonPath("$[0].rateDate").value("2026-01-12"));
    }

    @Test
    void returnsEmptyArrayWhenNorthRates() throws Exception {
        // AC5: empty DB -> HTTP 200 with []
        when(repo.findLatestRates()).thenReturn(List.of());

        mvc.perform(get("/api/rates"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void singlePairLookupReturnsEurUsdCheckpoint() throws Exception {
        // 02-pair-lookup AC1: GET /api/rates/EUR/USD -> 200 + one object, rate 1.0818
        when(repo.findRate("EUR", "USD"))
                .thenReturn(Optional.of(new Rate("EUR", "USD", "1.0818", "2026-01-12")));

        mvc.perform(get("/api/rates/EUR/USD"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.base").value("EUR"))
                .andExpect(jsonPath("$.quote").value("USD"))
                .andExpect(jsonPath("$.rate").value("1.0818"))
                .andExpect(jsonPath("$.rateDate").value("2026-01-12"));
    }

    @Test
    void singlePairLookupReturns404ForUnknownPair() throws Exception {
        // 02-pair-lookup AC2: unknown pair -> 404 + JSON {error}, no stack trace
        when(repo.findRate("EUR", "XXX")).thenReturn(Optional.empty());

        mvc.perform(get("/api/rates/EUR/XXX"))
                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.error").exists())
                                .andExpect(jsonPath("$.trace").doesNotExist());
    }
}
