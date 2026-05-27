package com.otptgbot.DTO;

import lombok.Data;

@Data
public class VerifyPayload {
    private String operationId;
    private String otpCode;
}