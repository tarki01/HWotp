package com.otptgbot.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "otp_config")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GlobalParams {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code_length", nullable = false)
    private int codeLength;

    @Column(name = "ttl_seconds", nullable = false)
    private int ttlSeconds;
}