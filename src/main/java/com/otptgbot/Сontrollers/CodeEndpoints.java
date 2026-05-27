package com.otptgbot.Сontrollers;

import com.otptgbot.DTO.CodeRequestPayload;
import com.otptgbot.DTO.VerifyPayload;
import com.otptgbot.MainServices.CodeGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/otp")
@PreAuthorize("hasRole('USER')")
@RequiredArgsConstructor
public class CodeEndpoints {

    private final CodeGenerator codeGenerator;

    @PostMapping("/generate")
    public ResponseEntity<String> createOtpCode(
            @AuthenticationPrincipal UserDetails currentUser,
            @RequestBody CodeRequestPayload generateRequest) {
        codeGenerator.generateAndSend(currentUser.getUsername(), generateRequest);
        return ResponseEntity.ok("OTP generated and sent");
    }

    @PostMapping("/validate")
    public ResponseEntity<String> verifyOtpCode(
            @AuthenticationPrincipal UserDetails currentUser,
            @RequestBody VerifyPayload validateRequest) {
        boolean isValid = codeGenerator.validate(currentUser.getUsername(), validateRequest);
        return isValid
                ? ResponseEntity.ok("OTP is valid")
                : ResponseEntity.badRequest().body("OTP is invalid or expired");
    }
}