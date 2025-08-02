package com.finefinee.websocket.domain.chat.model;

import lombok.Data;

@Data
public class ChatMessage {
    private String sender;
    private String content;
}
