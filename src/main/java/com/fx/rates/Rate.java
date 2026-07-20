package com.fx.rates;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Rate model — represents the latest exchange rate for a currency pair.
 * Matches the acceptance criteria: {base, quote, rate, rateDate}.
 */
public record Rate(
        @JsonProperty("base") String base,
        @JsonProperty("quote") String quote,
        @JsonProperty("rate") String rate,
        @JsonProperty("rateDate") String rateDate) {
}
