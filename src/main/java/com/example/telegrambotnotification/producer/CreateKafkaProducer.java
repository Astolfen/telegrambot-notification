package com.example.telegrambotnotification.producer;

import com.example.telegrambotnotification.dto.ChatRegisterEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class CreateKafkaProducer {
    private final KafkaTemplate<String, ChatRegisterEvent> kafkaTemplate;

    public void sendNotificationEvent(ChatRegisterEvent event) throws ExecutionException, InterruptedException {
        kafkaTemplate.send("chat-create-topic", event).get();
    }
}
