package com.example.naming.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Hibernate ddl-auto=update 不会修改已存在列的长度，历史表结构需要在此补齐。
 */
@Component
public class SchemaMigration implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(SchemaMigration.class);

    private final JdbcTemplate jdbcTemplate;

    public SchemaMigration(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(ApplicationArguments args) {
        widenColumn("name_record", "surname", 4, "VARCHAR(4) NOT NULL");
        widenColumn("name_record", "full_name", 10, "VARCHAR(10) NOT NULL");
    }

    private void widenColumn(String table, String column, int requiredLength, String definition) {
        try {
            Integer currentLength = jdbcTemplate.queryForObject(
                "SELECT CHARACTER_MAXIMUM_LENGTH FROM information_schema.COLUMNS " +
                    "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = ? AND COLUMN_NAME = ?",
                Integer.class, table, column);
            if (currentLength != null && currentLength < requiredLength) {
                jdbcTemplate.execute("ALTER TABLE " + table + " MODIFY COLUMN " + column + " " + definition);
                log.info("Schema migration: {}.{} {} -> {}", table, column, currentLength, requiredLength);
            }
        } catch (Exception e) {
            log.warn("Schema migration skipped for {}.{}: {}", table, column, e.getMessage());
        }
    }
}
