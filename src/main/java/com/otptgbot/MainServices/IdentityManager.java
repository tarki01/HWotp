package com.otptgbot.MainServices;

import com.otptgbot.DAO.AccountRepo;
import com.otptgbot.DTO.AuthRequestData;
import com.otptgbot.DTO.SignUpData;
import com.otptgbot.Entities.Account;
import com.otptgbot.Attributes.AccessLvl;
import com.otptgbot.SecurityUtilities.TokenHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class IdentityManager {

    private final AccountRepo accountRepo;
    private final PasswordEncoder passwordEncoder;
    private final TokenHandler tokenHandler;

    public void register(SignUpData signUpData) {
        if (accountRepo.findByLogin(signUpData.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Login already taken: " + signUpData.getUsername());
        }
        if (signUpData.getUserRole() == AccessLvl.ADMIN && accountRepo.existsByRole(AccessLvl.ADMIN)) {
            throw new IllegalStateException("Admin already exists");
        }
        Account newAccount = Account.builder()
                .login(signUpData.getUsername())
                .passwordHash(passwordEncoder.encode(signUpData.getPassword()))
                .accessLvl(signUpData.getUserRole())
                .email(signUpData.getEmail())
                .phone(signUpData.getPhoneNumber())
                .telegramChatId(signUpData.getTelegramChatId())
                .build();
        accountRepo.store(newAccount);
        log.info("Registered new user login={} role={}", newAccount.getLogin(), newAccount.getAccessLvl());
    }

    public String login(AuthRequestData authRequestData) {
        Account existingAccount = accountRepo.findByLogin(authRequestData.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));
        if (!passwordEncoder.matches(authRequestData.getPassword(), existingAccount.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid credentials");
        }
        String generatedToken = tokenHandler.generateToken(existingAccount.getLogin(), existingAccount.getAccessLvl().name());
        log.info("User logged in: {}", existingAccount.getLogin());
        return generatedToken;
    }
}