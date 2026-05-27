package com.otptgbot.DAO;

import com.otptgbot.Entities.Account;
import com.otptgbot.Attributes.AccessLvl;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class AccountRepo {

    private final JdbcTemplate jdbcTemplate;

    private static final RowMapper<Account> userRowMapper = (resultSet, rowNumber) -> Account.builder()
            .id(resultSet.getLong("id"))
            .login(resultSet.getString("login"))
            .passwordHash(resultSet.getString("password_hash"))
            .accessLvl(AccessLvl.valueOf(resultSet.getString("role")))
            .email(resultSet.getString("email"))
            .phone(resultSet.getString("phone"))
            .telegramChatId(resultSet.getString("telegram_chat_id"))
            .build();

    public Account store(Account account) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO users (login, password_hash, role, email, phone, telegram_chat_id) " +
                            "VALUES (?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, account.getLogin());
            preparedStatement.setString(2, account.getPasswordHash());
            preparedStatement.setString(3, account.getAccessLvl().name());
            preparedStatement.setString(4, account.getEmail());
            preparedStatement.setString(5, account.getPhone());
            preparedStatement.setString(6, account.getTelegramChatId());
            return preparedStatement;
        }, keyHolder);
        account.setId(((Number) keyHolder.getKeys().get("id")).longValue());
        return account;
    }

    public Optional<Account> findByLogin(String userLogin) {
        List<Account> resultList = jdbcTemplate.query(
                "SELECT * FROM users WHERE login = ?", userRowMapper, userLogin);
        return resultList.stream().findFirst();
    }

    public boolean existsByRole(AccessLvl role) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM users WHERE role = ?", Integer.class, role.name());
        return count != null && count > 0;
    }

    public List<Account> findAllNonAdmins() {
        return jdbcTemplate.query(
                "SELECT * FROM users WHERE role != 'ADMIN'", userRowMapper);
    }

    public Optional<Account> findById(Long userId) {
        List<Account> resultList = jdbcTemplate.query(
                "SELECT * FROM users WHERE id = ?", userRowMapper, userId);
        return resultList.stream().findFirst();
    }

    public void deleteById(Long userId) {
        jdbcTemplate.update("DELETE FROM users WHERE id = ?", userId);
    }
}