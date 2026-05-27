package com.otptgbot.Сontrollers;

import com.otptgbot.DTO.AuthRequestData;
import com.otptgbot.DTO.AuthResponseData;
import com.otptgbot.DTO.SignUpData;
import com.otptgbot.MainServices.IdentityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class IdentityEndpoints {

    private final IdentityManager identityManager;

    @PostMapping("/register")
    public ResponseEntity<String> registerNewUser(@RequestBody SignUpData signUpData) {
        identityManager.register(signUpData);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseData> authenticateUser(@RequestBody AuthRequestData authRequestData) {
        String accessToken = identityManager.login(authRequestData);
        return ResponseEntity.ok(new AuthResponseData(accessToken));
    }
}