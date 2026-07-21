package com.fx.transfer;

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

@WebMvcTest(TransferController.class)
class TransferControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    TransferRepository repo;

    @Test
    void returnsTransfersNewestFirst() throws Exception {
        when(repo.findAllNewestFirst()).thenReturn(List.of(
                new Transfer(203, 1, 2, "325.10", "USD", "2026-07-21 15:03:00", "COMPLETED"),
                new Transfer(202, 1, 2, "210.50", "USD", "2026-07-21 15:02:00", "COMPLETED"),
                new Transfer(201, 1, 2, "108.18", "USD", "2026-07-21 15:01:00", "COMPLETED")));

        mvc.perform(get("/api/transfers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(203))
                .andExpect(jsonPath("$[0].executedAt").value("2026-07-21 15:03:00"))
                .andExpect(jsonPath("$[1].id").value(202))
                .andExpect(jsonPath("$[2].id").value(201));
    }

    @Test
    void returnsEmptyArrayWhenNoTransfers() throws Exception {
        when(repo.findAllNewestFirst()).thenReturn(List.of());

        mvc.perform(get("/api/transfers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }
}