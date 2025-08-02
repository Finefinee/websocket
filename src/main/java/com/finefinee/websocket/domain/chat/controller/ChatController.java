package com.finefinee.websocket.domain.chat.controller;

import com.finefinee.websocket.domain.chat.model.ChatMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {

    // 클라이언트가 "/app/chat.send"로 메시지를 보내면 이 메서드가 실행됨
    @MessageMapping("/chat.send")
    @SendTo("/topic/messages") // 모든 구독자에게 브로드캐스트
    public ChatMessage sendMessage(ChatMessage message) {
        return message; // 받은 메시지를 그대로 모든 클라이언트에게 전달
    }
}
