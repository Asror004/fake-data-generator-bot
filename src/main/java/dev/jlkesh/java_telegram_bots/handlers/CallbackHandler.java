package dev.jlkesh.java_telegram_bots.handlers;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Update;
import dev.jlkesh.java_telegram_bots.config.TelegramBotConfiguration;
import dev.jlkesh.java_telegram_bots.state.DefaultState;
import dev.jlkesh.java_telegram_bots.state.GenerateDataState;
import dev.jlkesh.java_telegram_bots.state.RegistrationState;
import dev.jlkesh.java_telegram_bots.state.State;

import static dev.jlkesh.java_telegram_bots.config.ThreadSafeBeansContainer.*;

public class CallbackHandler implements Handler {
    private final TelegramBot bot = TelegramBotConfiguration.get();


    @Override
    public void handle(Update update) {
        CallbackQuery callbackQuery = update.callbackQuery();
        Long chatID = callbackQuery.message().chat().id();
        State state = userState.get(chatID);
        if ( state instanceof RegistrationState registrationState )
            registerUserCallbackProcessor.get().process(update, registrationState);
        else if ( state instanceof GenerateDataState generateDataState )
            generateDataCallbackProcessor.get().process(update, generateDataState);
        else if ( state instanceof DefaultState defaultState) {
            defaultCallbackProcessor.get().process(update, defaultState);
        }
    }
}
