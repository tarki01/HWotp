package com.otptgbot.DAO;

import com.otptgbot.Entities.GlobalParams;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class GlobalParamsRepo {

    private final JdbcTemplate jdbcTemplate;

    private static final RowMapper<GlobalParams> configRowMapper = (resultSet, rowNumber) -> GlobalParams.builder()
            .id(resultSet.getLong("id"))
            .codeLength(resultSet.getInt("code_length"))
            .ttlSeconds(resultSet.getInt("ttl_seconds"))
            .build();

    public GlobalParams retrieve() {
        return jdbcTemplate.queryForObject("SELECT * FROM otp_config LIMIT 1", configRowMapper);
    }

    public void modify(int newCodeLength, int newTtlSeconds) {
        jdbcTemplate.update("UPDATE otp_config SET code_length = ?, ttl_seconds = ?", newCodeLength, newTtlSeconds);
    }
}