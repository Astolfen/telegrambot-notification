package com.example.telegrambotnotification.service;

import com.example.telegrambotnotification.dto.ChatRegisterEvent;
import com.example.telegrambotnotification.producer.CreateKafkaProducer;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.concurrent.ExecutionException;

@Slf4j
public class MyTelegramBot extends TelegramLongPollingBot {

    private final String botUsername;

    private final CreateKafkaProducer createKafkaProducer;

    public MyTelegramBot(String botUsername, String botToken, CreateKafkaProducer createKafkaProducer) {
        super(botToken);
        this.botUsername = botUsername;
        this.createKafkaProducer = createKafkaProducer;
    }

    @Override
    public String getBotUsername() {
        return this.botUsername;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            String chatId = update.getMessage().getChatId().toString();
            String title = update.getMessage().getChat().getTitle();
            log.info(chatId);

            if ("/start".equals(messageText.trim())) {
//                String responseText = "Ваш chat id: " + chatId;
//                SendMessage message = SendMessage.builder()
//                        .chatId(chatId)
//                        .text(responseText)
//                        .build();
                try {
                    createKafkaProducer.sendNotificationEvent(new ChatRegisterEvent(chatId, title));
                } catch (InterruptedException e) {
                    log.error("Ошибка при отправке в kafka, поток прерван во премя ожидания:" + e.getMessage());
                } catch (ExecutionException e) {
                    log.error("Ошибка при отправке в kafka:" + e.getMessage());
                }
//                try {
//                    execute(message);
//
//                } catch (TelegramApiException e) {
//                    log.error("Ошибка отправки сообщения telegram: {}", e.getMessage());
//                }
            } else {
                log.info("Сообщения: {}", messageText);
            }
        }
    }
}
