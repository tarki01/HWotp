package com.otptgbot.Scheduler;

import com.otptgbot.DAO.SecretEntryRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CleanupTimer {

    private final SecretEntryRepo secretEntryRepo;

    @Scheduled(fixedRateString = "${otp.expiration.check-interval-ms}")
    public void expireOverdueOtpCodes() {
        int expiredCount = secretEntryRepo.markOverdueAsExpired();
        if (expiredCount > 0) {
            log.info("Marked {} OTP code(s) as EXPIRED", expiredCount);
        }
    }
}