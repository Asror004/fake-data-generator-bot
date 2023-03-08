package dev.jlkesh.java_telegram_bots.handlers;

import com.pengrad.telegrambot.model.Update;

public interface Handler {
    void handle(Update update);
}
