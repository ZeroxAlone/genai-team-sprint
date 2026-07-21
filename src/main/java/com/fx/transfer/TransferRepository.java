package com.fx.transfer;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Transfer persistence with JdbcTemplate following the sample repository pattern.
 */
@Repository
public class TransferRepository {

    private static final int DEFAULT_FROM_ACCOUNT = 1;
    private static final int DEFAULT_TO_ACCOUNT = 2;
    private static final String DEFAULT_STATUS = "COMPLETED";

    private static final RowMapper<Transfer> MAPPER = (rs, rowNum) -> new Transfer(
            rs.getLong("id"),
            rs.getInt("from_account"),
            rs.getInt("to_account"),
            rs.getString("amount"),
            rs.getString("currency_code"),
            rs.getString("executed_at"),
            rs.getString("status"));

    private final JdbcTemplate jdbc;

    public TransferRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public int addConvertedAmount(String amount, String currencyCode) {
        return jdbc.update(
                """
                INSERT INTO transfer (from_account, to_account, amount, currency_code, executed_at, status)
                VALUES (?, ?, ?, ?, ?, ?)
                """,
                DEFAULT_FROM_ACCOUNT,
                DEFAULT_TO_ACCOUNT,
                amount,
                currencyCode,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                DEFAULT_STATUS);
    }

    public List<Transfer> findAllNewestFirst() {
        return jdbc.query(
                """
                SELECT id,
                       from_account,
                       to_account,
                       amount,
                       currency_code,
                       DATE_FORMAT(executed_at, '%Y-%m-%d %H:%i:%s') AS executed_at,
                       status
                FROM transfer
                ORDER BY executed_at DESC, id DESC
                """,
                MAPPER);
    }
}