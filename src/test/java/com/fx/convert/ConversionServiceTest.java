package com.fx.convert;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the fee-tier logic in ConversionService.
 * No Spring context needed — pure Java.
 *
 * Tiers (on converted amount):
 *   < 1 000       → 1.00 %  (floor 1.00)
 *   1 000 – 9 999 → 0.50 %  (floor 1.00)
 *   ≥ 10 000      → 0.25 %  (floor 1.00)
 */
class ConversionServiceTest {

    private final ConversionService service = new ConversionService();

    // --- checkpoint required by spec ---

    @Test
    void checkpoint_amount100_fee1_00() {
        // 100 × 1 % = 1.00 — meets the floor exactly
        assertFee("100.00", "1.00");
    }

    @Test
    void checkpoint_amount5000_fee25_00() {
        // 5000 × 0.5 % = 25.00
        assertFee("5000.00", "25.00");
    }

    // --- tier 1: < 1 000, rate 1 % ---

    @Test
    void tier1_smallAmount_appliesMinFee() {
        // 50 × 1 % = 0.50 → floor → 1.00
        assertFee("50.00", "1.00");
    }

    @Test
    void tier1_justBelow1000() {
        // 999 × 1 % = 9.99
        assertFee("999.00", "9.99");
    }

    // --- tier 2: 1 000 – 9 999, rate 0.5 % ---

    @Test
    void tier2_atLowerBoundary() {
        // 1000 × 0.5 % = 5.00
        assertFee("1000.00", "5.00");
    }

    @Test
    void tier2_justBelow10000() {
        // 9999 × 0.5 % = 49.995 → rounds to 50.00
        assertFee("9999.00", "50.00");
    }

    // --- tier 3: ≥ 10 000, rate 0.25 % ---

    @Test
    void tier3_atLowerBoundary() {
        // 10000 × 0.25 % = 25.00
        assertFee("10000.00", "25.00");
    }

    @Test
    void tier3_largeAmount() {
        // 100000 × 0.25 % = 250.00
        assertFee("100000.00", "250.00");
    }

    // --- min-fee floor ---

    @Test
    void minFee_whenCalculatedFeeIsBelowFloor() {
        // 1 × 1 % = 0.01 → floor → 1.00
        assertFee("1.00", "1.00");
    }

    // helper
    private void assertFee(String convertedAmount, String expectedFee) {
        BigDecimal actual = service.calculateFee(new BigDecimal(convertedAmount));
        assertThat(actual).isEqualByComparingTo(new BigDecimal(expectedFee));
    }
}
