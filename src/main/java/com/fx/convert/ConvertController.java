package com.fx.convert;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

import com.fx.transfer.TransferRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * GET /api/convert?base=EUR&quote=USD&amount=100
 * Returns: { amount, rate, converted, fee, total }
 */
@RestController
public class ConvertController {

    private final JdbcTemplate jdbc;
    private final ConversionService conversionService;
    private final TransferRepository transferRepository;

    public ConvertController(JdbcTemplate jdbc,
                             ConversionService conversionService,
                             TransferRepository transferRepository) {
        this.jdbc = jdbc;
        this.conversionService = conversionService;
        this.transferRepository = transferRepository;
    }

    @GetMapping("/api/convert")
    public Map<String, Object> convert(
            @RequestParam String base,
            @RequestParam String quote,
            @RequestParam BigDecimal amount) {

        // AC5: amount ≤ 0 → 400
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("amount must be greater than zero");
        }

        // Latest rate for the pair
        List<BigDecimal> rates = jdbc.query(
                "SELECT rate FROM fx_rate WHERE base_code = ? AND quote_code = ? " +
                "ORDER BY rate_date DESC LIMIT 1",
                (rs, rowNum) -> rs.getBigDecimal("rate"),
                base.toUpperCase(), quote.toUpperCase());

        if (rates.isEmpty()) {
            throw new IllegalArgumentException(
                    "no rate found for " + base.toUpperCase() + "/" + quote.toUpperCase());
        }

        BigDecimal rate      = rates.get(0);
        BigDecimal converted = amount.multiply(rate).setScale(2, RoundingMode.HALF_UP);
        BigDecimal fee       = conversionService.calculateFee(converted);
        BigDecimal total     = converted.add(fee);

        transferRepository.addConvertedAmount(converted.toPlainString(), quote.toUpperCase());

        return Map.of(
                "amount",    amount,
                "rate",      rate,
                "converted", converted,
                "fee",       fee,
                "total",     total);
    }
}
