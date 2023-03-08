package dev.jlkesh.java_telegram_bots;

import com.pengrad.telegrambot.UpdatesListener;
import dev.jlkesh.java_telegram_bots.config.InitializerConfiguration;
import dev.jlkesh.java_telegram_bots.config.TelegramBotConfiguration;
import dev.jlkesh.java_telegram_bots.handlers.UpdateHandler;

public class Main {
    public static void main(String[] args) {
        InitializerConfiguration.init();
        UpdateHandler updateHandler = new UpdateHandler();
        TelegramBotConfiguration.get().setUpdatesListener((updates) -> {


            try {
                updateHandler.handle(updates);
                return UpdatesListener.CONFIRMED_UPDATES_ALL;
            }catch (Exception ex){
                ex.printStackTrace();
            }
            return 1;
        });
    }
}