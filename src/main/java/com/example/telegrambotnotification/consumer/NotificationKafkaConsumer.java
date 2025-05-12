package com.example.telegrambotnotification.consumer;

import com.example.telegrambotnotification.dto.NotificationEvent;
import com.example.telegrambotnotification.service.DuplicateNotificationGuard;
import com.example.telegrambotnotification.service.TelegramBotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@KafkaListener(topics = "${kafka.topic.notification}")
public class NotificationKafkaConsumer {

    private final TelegramBotService telegramBotService;
    private final DuplicateNotificationGuard duplicateNotificationGuard;


    @KafkaHandler(isDefault = true)
    public void handle(ConsumerRecord<String, NotificationEvent> record) {
        NotificationEvent message = record.value();
        if (message == null) {
            Header errorHeader = record.headers().lastHeader("springDeserializerExceptionValue");
            if (errorHeader != null) {
                String errorMessage = new String(errorHeader.value());
                log.error("Ошибка десериализации сообщения: {}", errorMessage);
            } else {
                log.warn("Получено null-сообщение без дополнительной информации об ошибке десериализации");
            }
            return;
        }


        if (message.getMessageId() == null || message.getMessageId().isBlank()) {
            log.warn("Получено уведомление без id");
            return;
        }
        if (duplicateNotificationGuard.isDuplicate(message.getMessageId())) {
            log.warn("Получено дублирующее уведомление с id: {}", message.getMessageId());
            return;
        }

        // Отметим уведомление как обработанное, чтобы последующие дубли не прошли проверку.
        duplicateNotificationGuard.markProcessed(message.getMessageId());
        log.info("Получено уведомление: {}", message);
        message.getChats().forEach(chatId ->
                telegramBotService.sendNotification(chatId, message.getMessage())
        );
    }
}
