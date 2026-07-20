package com.fx.convert;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Service;

/**
 * Fee-tier logic for retail FX conversions.
 * Tiers applied to the converted (quote) amount:
 *   < 1 000  → 1.00 %
 *   1 000 – 9 999 → 0.50 %
 *   ≥ 10 000 → 0.25 %
 * Floor: fee is never less than 1.00.
 */
@Service
public class ConversionService {

    private static final BigDecimal TIER1_RATE = new BigDecimal("0.01");
    private static final BigDecimal TIER2_RATE = new BigDecimal("0.005");
    private static final BigDecimal TIER3_RATE = new BigDecimal("0.0025");
    private static final BigDecimal MIN_FEE    = new BigDecimal("1.00");

    private static final BigDecimal TIER2_THRESHOLD = new BigDecimal("1000");
    private static final BigDecimal TIER3_THRESHOLD = new BigDecimal("10000");

    /**
     * Calculate the fee on a converted amount, rounded to 2 d.p.
     * The fee basis is the converted (quote-currency) amount.
     */
    public BigDecimal calculateFee(BigDecimal convertedAmount) {
        BigDecimal rate;
        if (convertedAmount.compareTo(TIER2_THRESHOLD) < 0) {
            rate = TIER1_RATE;
        } else if (convertedAmount.compareTo(TIER3_THRESHOLD) < 0) {
            rate = TIER2_RATE;
        } else {
            rate = TIER3_RATE;
        }
        BigDecimal fee = convertedAmount.multiply(rate).setScale(2, RoundingMode.HALF_UP);
        return fee.max(MIN_FEE);
    }
}
