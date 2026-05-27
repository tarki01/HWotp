package com.otptgbot.MainServices;

import com.otptgbot.DAO.SecretEntryRepo;
import com.otptgbot.DAO.AccountRepo;
import com.otptgbot.Entities.Account;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountManager {

    private final AccountRepo accountRepo;
    private final SecretEntryRepo secretEntryRepo;

    public List<Account> getAllNonAdmins() {
        return accountRepo.findAllNonAdmins();
    }

    public void deleteUser(Long userId) {
        accountRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        secretEntryRepo.removeByUserId(userId);
        accountRepo.deleteById(userId);
        log.info("Deleted user id={} and their OTP codes", userId);
    }
}