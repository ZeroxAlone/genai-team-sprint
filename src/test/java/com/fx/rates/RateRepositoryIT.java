package com.fx.rates;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.MountableFile;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Rate repository integration test (runs on `./mvnw verify`, needs Docker).
 * Uses a real MySQL database with the seed data to test the "latest per pair" query.
 *
 * AC2: exactly one row per pair (the one with max rate_date).
 * AC3: EUR/USD rate=1.0818, rateDate="2026-01-12" (the checkpoint values).
 */
@SpringBootTest
@Testcontainers(disabledWithoutDocker = true)
class RateRepositoryIT {

    @Container
    static final MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8")
            .withDatabaseName("fxdb")
            .withCopyFileToContainer(
                    MountableFile.forHostPath("ops/fxdb-seed.sql"),
                    "/docker-entrypoint-initdb.d/01-seed.sql");

    @DynamicPropertySource
    static void datasource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
    }

    @Autowired
    RateRepository repo;

    @Test
    void loadsTheLatestRatesFromARealDb() {
        // The seed has 30 rates, but they're historical (3 date levels per pair).
        // findLatestRates() should return only ONE per pair. There are 10 unique pairs in the seed.
        List<Rate> rates = repo.findLatestRates();
        assertThat(rates).hasSize(10);  // 10 unique (base, quote) pairs
    }

    @Test
    void eurUsdIsTheLatestCheckpoint() {
        // AC3: EUR/USD reads rate = 1.0818, rateDate = "2026-01-12"
        List<Rate> rates = repo.findLatestRates();
        assertThat(rates)
                .anyMatch(r -> "EUR".equals(r.base())
                        && "USD".equals(r.quote())
                        && "1.0818".equals(r.rate())
                        && "2026-01-12".equals(r.rateDate()));
    }

    @Test
    void eachPairAppearsOnce() {
        // AC2: exactly one row per pair
        List<Rate> rates = repo.findLatestRates();
        // Count occurrences of each (base, quote) pair; all should be 1.
        assertThat(rates.stream()
                .map(r -> r.base() + "/" + r.quote())
                .distinct()
                .count())
                .isEqualTo(rates.size());  // no duplicates
    }

    @Test
    void allRatesAreFromTheLatestDate() {
        // All rates should be from 2026-01-12 (the latest date in the seed).
        List<Rate> rates = repo.findLatestRates();
        assertThat(rates)
                .allMatch(r -> "2026-01-12".equals(r.rateDate()));
    }

    @Test
    void findRateReturnsTheEurUsdCheckpoint() {
        // 02-pair-lookup AC1: EUR/USD -> rate 1.0818
        Optional<Rate> rate = repo.findRate("EUR", "USD");
        assertThat(rate).isPresent();
        assertThat(rate.get().rate()).isEqualTo("1.0818");
        assertThat(rate.get().rateDate()).isEqualTo("2026-01-12");
    }

    @Test
    void findRateIsEmptyForAnUnknownPair() {
        // 02-pair-lookup AC2: unknown pair -> empty (controller turns this into a 404)
        Optional<Rate> rate = repo.findRate("EUR", "XXX");
        assertThat(rate).isEmpty();
    }

    @Test
    void findRateHistoryReturnsEurUsdOldestToNewest() {
        // 08 AC1/AC2: EUR/USD history is 3 rows, sorted oldest -> newest, ending at 1.0818
        List<Rate> history = repo.findRateHistory("EUR", "USD");

        assertThat(history).hasSize(3);
        assertThat(history.get(0).rateDate()).isEqualTo("2026-01-10");
        assertThat(history.get(1).rateDate()).isEqualTo("2026-01-11");
        assertThat(history.get(2).rateDate()).isEqualTo("2026-01-12");
        assertThat(history.get(2).rate()).isEqualTo("1.0818");
    }

    @Test
    void findRateHistoryReturnsEmptyForUnknownPair() {
        // 08 AC3: unknown pair history is empty
        List<Rate> history = repo.findRateHistory("EUR", "XXX");
        assertThat(history).isEmpty();
    }
}
