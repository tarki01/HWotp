package com.otptgbot.MainServices;

import com.otptgbot.DAO.SecretEntryRepo;
import com.otptgbot.DAO.GlobalParamsRepo;
import com.otptgbot.DAO.AccountRepo;
import com.otptgbot.DTO.CodeRequestPayload;
import com.otptgbot.DTO.SettingsPayload;
import com.otptgbot.DTO.VerifyPayload;
import com.otptgbot.Entities.SecretEntry;
import com.otptgbot.Entities.GlobalParams;
import com.otptgbot.Entities.Account;
import com.otptgbot.Attributes.State;
import com.otptgbot.NotificationServices.MailDispatcher;
import com.otptgbot.NotificationServices.LocalStorageWriter;
import com.otptgbot.NotificationServices.TextMessageSender;
import com.otptgbot.NotificationServices.BotMessenger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class CodeGenerator {

    private final SecretEntryRepo secretEntryRepo;
    private final GlobalParamsRepo globalParamsRepo;
    private final AccountRepo accountRepo;
    private final MailDispatcher mailDispatcher;
    private final TextMessageSender smsNotificationService;
    private final BotMessenger telegramNotificationService;
    private final LocalStorageWriter localStorageWriter;

    private static final SecureRandom RANDOM_NUMBER_GENERATOR = new SecureRandom();

    public void generateAndSend(String userLogin, CodeRequestPayload generateRequest) {
        Account requestingAccount = accountRepo.findByLogin(userLogin)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        GlobalParams currentConfig = globalParamsRepo.retrieve();

        String generatedCode = generateNumericCode(currentConfig.getCodeLength());
        LocalDateTime currentTime = LocalDateTime.now();
        SecretEntry newSecretEntry = SecretEntry.builder()
                .userId(requestingAccount.getId())
                .operationId(generateRequest.getOperationId())
                .code(generatedCode)
                .state(State.ACTIVE)
                .createdAt(currentTime)
                .expiresAt(currentTime.plusSeconds(currentConfig.getTtlSeconds()))
                .build();
        secretEntryRepo.store(newSecretEntry);
        log.info("Generated OTP for user={} operation={}", userLogin, generateRequest.getOperationId());

        String deliveryDestination = generateRequest.getTargetAddress();
        switch (generateRequest.getDeliveryChannel()) {
            case EMAIL    -> mailDispatcher.sendCode(deliveryDestination, generatedCode);
            case SMS      -> smsNotificationService.sendCode(deliveryDestination, generatedCode);
            case TELEGRAM -> telegramNotificationService.sendCode(deliveryDestination, generatedCode);
            case FILE     -> localStorageWriter.sendCode(userLogin, generatedCode);
        }
    }

    public boolean validate(String userLogin, VerifyPayload validateRequest) {
        Account validatingAccount = accountRepo.findByLogin(userLogin)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        SecretEntry existingSecretEntry = secretEntryRepo
                .findActiveByUserAndOperation(validatingAccount.getId(), validateRequest.getOperationId())
                .orElse(null);

        if (existingSecretEntry == null) {
            log.warn("No active OTP for user={} operation={}", userLogin, validateRequest.getOperationId());
            return false;
        }
        if (existingSecretEntry.getExpiresAt().isBefore(LocalDateTime.now())) {
            secretEntryRepo.changeStatus(existingSecretEntry.getId(), State.EXPIRED);
            log.warn("OTP expired for user={} operation={}", userLogin, validateRequest.getOperationId());
            return false;
        }
        if (!existingSecretEntry.getCode().equals(validateRequest.getOtpCode())) {
            log.warn("Wrong OTP code for user={} operation={}", userLogin, validateRequest.getOperationId());
            return false;
        }

        secretEntryRepo.changeStatus(existingSecretEntry.getId(), State.USED);
        log.info("OTP validated successfully for user={} operation={}", userLogin, validateRequest.getOperationId());
        return true;
    }

    public void updateConfig(SettingsPayload configRequest) {
        globalParamsRepo.modify(configRequest.getCodeLength(), configRequest.getTtlSeconds());
        log.info("OTP config updated: length={} ttl={}s", configRequest.getCodeLength(), configRequest.getTtlSeconds());
    }

    private String generateNumericCode(int codeLength) {
        StringBuilder codeBuilder = new StringBuilder(codeLength);
        for (int i = 0; i < codeLength; i++) {
            codeBuilder.append(RANDOM_NUMBER_GENERATOR.nextInt(10));
        }
        return codeBuilder.toString();
    }
}