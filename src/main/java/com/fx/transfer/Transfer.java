package com.fx.transfer;

/**
 * One transfer row as returned by GET /api/transfers.
 */
public record Transfer(
        long id,
        int fromAccount,
        int toAccount,
        String amount,
        String currency,
        String executedAt,
        String status) {
}