package com.example.telegrambotnotification.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


@Service
@RequiredArgsConstructor
@Slf4j
public class TelegramBotService {
    private final MyTelegramBot myTelegramBot;

    public void sendNotification(String chatId, String text) {
        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .build();
        try {
            myTelegramBot.execute(message);
            log.info("Уведомление отправлено в чат: {}", chatId);
        } catch (TelegramApiException e) {
            log.error("Ошибка отправки сообщения в чат {}: {}", chatId, e.getMessage());
        }
    }
}
