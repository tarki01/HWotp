package com.otptgbot.DAO;

import com.otptgbot.Entities.SecretEntry;
import com.otptgbot.Attributes.State;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class SecretEntryRepo {

    private final JdbcTemplate jdbcTemplate;

    private static final RowMapper<SecretEntry> codeRowMapper = (resultSet, rowNumber) -> SecretEntry.builder()
            .id(resultSet.getLong("id"))
            .userId(resultSet.getLong("user_id"))
            .operationId(resultSet.getString("operation_id"))
            .code(resultSet.getString("code"))
            .state(State.valueOf(resultSet.getString("status")))
            .createdAt(resultSet.getTimestamp("created_at").toLocalDateTime())
            .expiresAt(resultSet.getTimestamp("expires_at").toLocalDateTime())
            .build();

    public SecretEntry store(SecretEntry secretEntry) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO otp_codes (user_id, operation_id, code, status, created_at, expires_at) " +
                            "VALUES (?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setLong(1, secretEntry.getUserId());
            preparedStatement.setString(2, secretEntry.getOperationId());
            preparedStatement.setString(3, secretEntry.getCode());
            preparedStatement.setString(4, secretEntry.getState().name());
            preparedStatement.setTimestamp(5, Timestamp.valueOf(secretEntry.getCreatedAt()));
            preparedStatement.setTimestamp(6, Timestamp.valueOf(secretEntry.getExpiresAt()));
            return preparedStatement;
        }, keyHolder);
        secretEntry.setId(((Number) keyHolder.getKeys().get("id")).longValue());
        return secretEntry;
    }

    public Optional<SecretEntry> findActiveByUserAndOperation(Long userId, String operationId) {
        List<SecretEntry> resultList = jdbcTemplate.query(
                "SELECT * FROM otp_codes WHERE user_id = ? AND operation_id = ? AND status = 'ACTIVE'",
                codeRowMapper, userId, operationId);
        return resultList.stream().findFirst();
    }

    public void changeStatus(Long codeId, State newState) {
        jdbcTemplate.update("UPDATE otp_codes SET status = ? WHERE id = ?", newState.name(), codeId);
    }

    public int markOverdueAsExpired() {
        return jdbcTemplate.update(
                "UPDATE otp_codes SET status = 'EXPIRED' " +
                        "WHERE status = 'ACTIVE' AND expires_at < NOW()");
    }

    public void removeByUserId(Long userId) {
        jdbcTemplate.update("DELETE FROM otp_codes WHERE user_id = ?", userId);
    }
}