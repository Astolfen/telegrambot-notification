package com.example.telegrambotnotification.service;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Component
public class DuplicateNotificationGuard {

    private final ConcurrentHashMap<String, Long> processedMessages = new ConcurrentHashMap<>();

    private static final long EXPIRATION_TIME_MS = TimeUnit.DAYS.toMillis(7);

    /**
     * Проверяет, было ли уведомление с таким id уже обработано.
     *
     * @param notificationId уникальный идентификатор уведомления
     * @return true, если уведомление уже было обработано, иначе false
     */
    public boolean isDuplicate(String notificationId) {
        long now = System.currentTimeMillis();
        // Очистка устаревших записей
        processedMessages.entrySet().removeIf(entry -> now - entry.getValue() > EXPIRATION_TIME_MS);
        return processedMessages.containsKey(notificationId);
    }

    public void markProcessed(String notificationId) {
        processedMessages.put(notificationId, System.currentTimeMillis());
    }
}
