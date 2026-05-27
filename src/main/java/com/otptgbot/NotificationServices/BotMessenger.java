package com.otptgbot.NotificationServices;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

@Service
@Slf4j
public class BotMessenger implements DeliveryContract {

    private final String telegramBotToken;

    public BotMessenger(@Value("${telegram.bot.token}") String botToken) {
        this.telegramBotToken = botToken;
    }

    @Override
    public void sendCode(String chatId, String otpCode) {
        String messageText = "Your OTP code: " + otpCode;
        String apiUrl = String.format(
                "https://api.telegram.org/bot%s/sendMessage?chat_id=%s&text=%s",
                telegramBotToken, chatId, URLEncoder.encode(messageText, StandardCharsets.UTF_8));

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .GET()
                .build();

        try {
            HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if (httpResponse.statusCode() != 200) {
                log.error("Telegram API error. Status: {}, body: {}", httpResponse.statusCode(), httpResponse.body());
                throw new RuntimeException("Telegram API returned status " + httpResponse.statusCode());
            }
            log.info("Telegram OTP sent to chatId={}", chatId);
        } catch (InterruptedException e) {
            log.error("Interrupted while sending Telegram message: {}", e.getMessage(), e);
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while sending Telegram message", e);
        } catch (IOException e) {
            log.error("IO error sending Telegram message: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to send Telegram message", e);
        }
    }
}