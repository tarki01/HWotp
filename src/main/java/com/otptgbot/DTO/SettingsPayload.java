package com.otptgbot.DTO;

import lombok.Data;

@Data
public class SettingsPayload {
    private int codeLength;
    private int ttlSeconds;
}