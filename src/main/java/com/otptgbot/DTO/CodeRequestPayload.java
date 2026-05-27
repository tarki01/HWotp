package com.otptgbot.DTO;

import com.otptgbot.Attributes.MsgRoute;
import lombok.Data;

@Data
public class CodeRequestPayload {
    private String operationId;
    private MsgRoute deliveryChannel;
    private String targetAddress;
}