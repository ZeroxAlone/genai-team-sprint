package com.fx.stats;

/**
 * Aggregate stats for the dashboard endpoint.
 */
public record ApiStats(long totalTransfers, String busiestCurrency, String latestRateDate) {
}
