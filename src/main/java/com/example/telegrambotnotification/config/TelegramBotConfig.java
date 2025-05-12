package com.example.telegrambotnotification.config;

import com.example.telegrambotnotification.producer.CreateKafkaProducer;
import com.example.telegrambotnotification.service.MyTelegramBot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
@Slf4j
public class TelegramBotConfig {
    @Bean
    public MyTelegramBot telegramBot(@Value("${telegram.bot.username}") String botName,
                                     @Value("${telegram.bot.token}") String botToken,
                                     CreateKafkaProducer createKafkaProducer) {
        MyTelegramBot bot = new MyTelegramBot(botName,botToken,createKafkaProducer);
        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(bot);
        } catch (TelegramApiException e) {
            log.error("Ошибка при обработке Telegram API: {}", e.getMessage());
        }
        return bot;
    }
}
