package com.fx.transfer;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TransferController {

    private final TransferRepository repo;

    public TransferController(TransferRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/api/transfers")
    public List<Transfer> all() {
        return repo.findAllNewestFirst();
    }
}