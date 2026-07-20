package com.fx.rates;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Rate repository — queries the fx_rate table for the latest exchange rate per pair.
 * Pattern: use JdbcTemplate with a RowMapper that converts a DB row to a Rate.
 * Follows the same shape as CurrencyRepository.
 */
@Repository
public class RateRepository {

    private final JdbcTemplate jdbc;

    private static final RowMapper<Rate> MAPPER = (rs, rowNum) -> new Rate(
            rs.getString("base_code"),
            rs.getString("quote_code"),
            rs.getString("rate"),
            rs.getString("rate_date"));

    public RateRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /**
     * Find the latest rate for each currency pair.
     * Query: for each (base_code, quote_code), pick the row with max rate_date.
     * Returns empty list if no rates exist (AC5: HTTP 200, not 500).
     */
    public List<Rate> findLatestRates() {
        String sql = """
                SELECT base_code, quote_code, rate, DATE_FORMAT(rate_date, '%Y-%m-%d') as rate_date
                FROM fx_rate
                WHERE (base_code, quote_code, rate_date) IN (
                  SELECT base_code, quote_code, MAX(rate_date)
                  FROM fx_rate
                  GROUP BY base_code, quote_code
                )
                ORDER BY base_code, quote_code
                """;
        return jdbc.query(sql, MAPPER);
    }

    /**
     * Find the latest rate for a single pair (02-pair-lookup AC1/AC2).
     * Returns empty when the pair doesn't exist -> controller turns that into a 404.
     */
    public Optional<Rate> findRate(String base, String quote) {
        String sql = """
                SELECT base_code, quote_code, rate, DATE_FORMAT(rate_date, '%Y-%m-%d') as rate_date
                FROM fx_rate
                WHERE base_code = ? AND quote_code = ?
                ORDER BY rate_date DESC
                LIMIT 1
                """;
        return jdbc.query(sql, MAPPER, base, quote).stream().findFirst();
    }
}
