package com.fx.stats;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * Repository for aggregate FX statistics used by GET /api/stats.
 */
@Repository
public class StatsRepository {

    private final JdbcTemplate jdbc;

    public StatsRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public ApiStats loadStats() {
        String sql = """
                SELECT
                  (SELECT COUNT(*) FROM transfer) AS total_transfers,
                  (SELECT currency_code
                     FROM transfer
                     GROUP BY currency_code
                     ORDER BY COUNT(*) DESC
                     LIMIT 1) AS busiest_currency,
                  (SELECT DATE_FORMAT(MAX(rate_date), '%Y-%m-%d')
                     FROM fx_rate) AS latest_rate_date
                """;

        return jdbc.queryForObject(sql, (rs, rowNum) -> new ApiStats(
                rs.getLong("total_transfers"),
                rs.getString("busiest_currency"),
                rs.getString("latest_rate_date")
        ));
    }
}
