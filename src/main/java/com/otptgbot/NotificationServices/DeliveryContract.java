package com.otptgbot.NotificationServices;

public interface DeliveryContract {
    void sendCode(String destination, String otpCode);
}