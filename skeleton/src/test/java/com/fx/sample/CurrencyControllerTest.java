package com.fx.sample;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * SAMPLE test — a web-slice test for the Currencies endpoint. It mocks the repository, so it
 * needs NO database and runs in CI. This is the pattern to copy when you test your own
 * controllers. (@WebMvcTest loads only the web layer, not the DataSource.)
 */
@WebMvcTest(CurrencyController.class)
class CurrencyControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    CurrencyRepository repo;

    @Test
    void returnsCurrenciesAsJson() throws Exception {
        when(repo.findAll()).thenReturn(List.of(
                new Currency("EUR", "Euro", "€"),
                new Currency("USD", "US Dollar", "$")));

        mvc.perform(get("/api/currencies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].code").value("EUR"))
                .andExpect(jsonPath("$[1].code").value("USD"));
    }
}
