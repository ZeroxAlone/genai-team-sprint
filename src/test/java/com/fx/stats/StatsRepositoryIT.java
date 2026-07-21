package com.fx.stats;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.MountableFile;

@SpringBootTest
@Testcontainers(disabledWithoutDocker = true)
class StatsRepositoryIT {

    @Container
    static final MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8")
            .withDatabaseName("fxdb")
            .withCopyFileToContainer(
                    MountableFile.forHostPath("ops/fxdb-seed.sql"),
                    "/docker-entrypoint-initdb.d/01-seed.sql");

    @DynamicPropertySource
    static void datasource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
    }

    @Autowired
    StatsRepository repo;

    @Autowired
    JdbcTemplate jdbc;

    @Test
    void returnsCheckpointTotalsAndLatestDate() {
        ApiStats stats = repo.loadStats();

        assertThat(stats.totalTransfers()).isEqualTo(200L);
        assertThat(stats.latestRateDate()).isEqualTo("2026-01-12");
    }

    @Test
    void busiestCurrencyMatchesReferenceSql() {
        ApiStats stats = repo.loadStats();

        String busiest = jdbc.queryForObject(
                "SELECT currency_code, COUNT(*) c FROM transfer GROUP BY currency_code ORDER BY c DESC LIMIT 1",
                (rs, rowNum) -> rs.getString("currency_code")
        );

        assertThat(stats.busiestCurrency()).isEqualTo(busiest);
    }
}
