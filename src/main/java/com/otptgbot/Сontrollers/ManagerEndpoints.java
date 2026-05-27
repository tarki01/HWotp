package com.otptgbot.Сontrollers;

import com.otptgbot.DTO.SettingsPayload;
import com.otptgbot.Entities.Account;
import com.otptgbot.MainServices.CodeGenerator;
import com.otptgbot.MainServices.AccountManager;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class ManagerEndpoints {

    private final AccountManager accountManager;
    private final CodeGenerator codeGenerator;

    @GetMapping("/users")
    public ResponseEntity<List<Account>> getAllUsers() {
        return ResponseEntity.ok(accountManager.getAllNonAdmins());
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<String> removeUser(@PathVariable Long userId) {
        accountManager.deleteUser(userId);
        return ResponseEntity.ok("User deleted");
    }

    @PutMapping("/otp-config")
    public ResponseEntity<String> modifyOtpConfiguration(@RequestBody SettingsPayload configRequest) {
        codeGenerator.updateConfig(configRequest);
        return ResponseEntity.ok("OTP configuration updated");
    }
}