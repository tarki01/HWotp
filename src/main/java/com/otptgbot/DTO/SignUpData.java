package com.otptgbot.DTO;

import com.otptgbot.Attributes.AccessLvl;
import lombok.Data;

@Data
public class SignUpData {
    private String username;
    private String password;
    private AccessLvl userRole;
    private String email;
    private String phoneNumber;
    private String telegramChatId;
}