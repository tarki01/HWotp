package com.otptgbot.NotificationServices;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;

@Service
@Slf4j
public class LocalStorageWriter implements DeliveryContract {

    private static final Path OTP_FILE_PATH = Path.of("otp_codes.txt");

    @Override
    public void sendCode(String destinationIdentifier, String otpCode) {
        String logLine = String.format("[%s] destination=%s code=%s%n",
                LocalDateTime.now(), destinationIdentifier, otpCode);
        try {
            Files.writeString(OTP_FILE_PATH, logLine, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            log.info("OTP code saved to file for destination={}", destinationIdentifier);
        } catch (IOException e) {
            log.error("Failed to write OTP code to file: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to write OTP to file", e);
        }
    }
}