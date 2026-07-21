package com.fx.convert;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.test.web.servlet.MockMvc;

import com.fx.transfer.TransferRepository;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Web-slice tests for GET /api/convert.
 * The JdbcTemplate is mocked so no database is needed.
 */
@WebMvcTest(ConvertController.class)
@Import(ConversionService.class)
class ConvertControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    JdbcTemplate jdbc;

        @MockBean
        TransferRepository transferRepository;

    // ConversionService is a real bean loaded by @WebMvcTest (it has no DB dependency)

    @Test
    void happyPath_eurUsd100_returns10818() throws Exception {
        // Seed checkpoint: EUR/USD latest rate = 1.0818
        when(jdbc.query(anyString(), ArgumentMatchers.<RowMapper<BigDecimal>>any(), eq("EUR"), eq("USD")))
                .thenReturn(List.of(new BigDecimal("1.0818")));
                when(transferRepository.addConvertedAmount("108.18", "USD")).thenReturn(1);

        mvc.perform(get("/api/convert")
                        .param("base", "EUR")
                        .param("quote", "USD")
                        .param("amount", "100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(100))
                .andExpect(jsonPath("$.rate").value(1.0818))
                // 100 × 1.0818 = 108.18
                .andExpect(jsonPath("$.converted").value(108.18))
                // fee: 108.18 × 1 % = 1.0818 → rounds to 1.08 but floor is 1.00, so 1.08
                .andExpect(jsonPath("$.fee").value(1.08))
                // total = 108.18 + 1.08 = 109.26
                .andExpect(jsonPath("$.total").value(109.26));

        verify(transferRepository).addConvertedAmount("108.18", "USD");
    }

    @Test
    void amountZero_returns400() throws Exception {
        mvc.perform(get("/api/convert")
                        .param("base", "EUR")
                        .param("quote", "USD")
                        .param("amount", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.trace").doesNotExist());

                verify(transferRepository, never()).addConvertedAmount(anyString(), anyString());
    }

    @Test
    void amountNegative_returns400() throws Exception {
        mvc.perform(get("/api/convert")
                        .param("base", "EUR")
                        .param("quote", "USD")
                        .param("amount", "-50"))
                .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").exists())
            .andExpect(jsonPath("$.trace").doesNotExist());

                verify(transferRepository, never()).addConvertedAmount(anyString(), anyString());
    }

    @Test
        void missingAmountParam_returns400() throws Exception {
        mvc.perform(get("/api/convert")
                .param("base", "EUR")
                .param("quote", "USD"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("missing required parameter: amount"))
            .andExpect(jsonPath("$.trace").doesNotExist());

        verify(transferRepository, never()).addConvertedAmount(anyString(), anyString());
        }

        @Test
        void nonNumericAmount_returns400() throws Exception {
        mvc.perform(get("/api/convert")
                .param("base", "EUR")
                .param("quote", "USD")
                .param("amount", "abc"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("invalid value for amount"))
            .andExpect(jsonPath("$.trace").doesNotExist());

        verify(transferRepository, never()).addConvertedAmount(anyString(), anyString());
        }

        @Test
        void unknownPair_returns404() throws Exception {
        when(jdbc.query(anyString(), ArgumentMatchers.<RowMapper<BigDecimal>>any(), eq("EUR"), eq("XYZ")))
                .thenReturn(List.of());

        mvc.perform(get("/api/convert")
                        .param("base", "EUR")
                        .param("quote", "XYZ")
                        .param("amount", "100"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error").exists())
            .andExpect(jsonPath("$.trace").doesNotExist());

                verify(transferRepository, never()).addConvertedAmount(anyString(), anyString());
    }
}
