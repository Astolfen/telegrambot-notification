package com.example.telegrambotnotification.dto;

import lombok.Data;


import java.util.List;

@Data
public class NotificationEvent {
    private String messageId;
    private List<String> chats; // Список ID чатов
    private NotificationType type;
    private String message;
}