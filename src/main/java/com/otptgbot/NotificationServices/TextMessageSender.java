package com.otptgbot.NotificationServices;

import lombok.extern.slf4j.Slf4j;
import org.jsmpp.bean.*;
import org.jsmpp.session.BindParameter;
import org.jsmpp.session.SMPPSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
@Slf4j
public class TextMessageSender implements DeliveryContract {

    private final String smppHost;
    private final int smppPort;
    private final String smppSystemId;
    private final String smppPassword;
    private final String smppSystemType;
    private final String smppSourceAddress;

    public TextMessageSender(
            @Value("${smpp.host}") String smppHost,
            @Value("${smpp.port}") int smppPort,
            @Value("${smpp.system_id}") String smppSystemId,
            @Value("${smpp.password}") String smppPassword,
            @Value("${smpp.system_type}") String smppSystemType,
            @Value("${smpp.source_addr}") String smppSourceAddress) {
        this.smppHost = smppHost;
        this.smppPort = smppPort;
        this.smppSystemId = smppSystemId;
        this.smppPassword = smppPassword;
        this.smppSystemType = smppSystemType;
        this.smppSourceAddress = smppSourceAddress;
    }

    @Override
    public void sendCode(String phoneNumber, String otpCode) {
        SMPPSession smppSession = new SMPPSession();
        try {
            BindParameter bindParam = new BindParameter(
                    BindType.BIND_TX, smppSystemId, smppPassword, smppSystemType,
                    TypeOfNumber.UNKNOWN, NumberingPlanIndicator.UNKNOWN, smppSourceAddress);
            smppSession.connectAndBind(smppHost, smppPort, bindParam);

            smppSession.submitShortMessage(
                    smppSystemType,
                    TypeOfNumber.UNKNOWN, NumberingPlanIndicator.UNKNOWN, smppSourceAddress,
                    TypeOfNumber.UNKNOWN, NumberingPlanIndicator.UNKNOWN, phoneNumber,
                    new ESMClass(), (byte) 0, (byte) 1, null, null,
                    new RegisteredDelivery(SMSCDeliveryReceipt.DEFAULT),
                    (byte) 0, new GeneralDataCoding(Alphabet.ALPHA_DEFAULT), (byte) 0,
                    ("Your OTP code: " + otpCode).getBytes(StandardCharsets.UTF_8));

            log.info("SMS OTP sent to {}", phoneNumber);
        } catch (Exception e) {
            log.error("Failed to send SMS to {}: {}", phoneNumber, e.getMessage(), e);
            throw new RuntimeException("Failed to send SMS", e);
        } finally {
            smppSession.unbindAndClose();
        }
    }
}