package dev.jlkesh.java_telegram_bots.handlers;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ForceReply;
import com.pengrad.telegrambot.request.SendMessage;
import dev.jlkesh.java_telegram_bots.config.TelegramBotConfiguration;
import dev.jlkesh.java_telegram_bots.dto.Dictionary;
import dev.jlkesh.java_telegram_bots.state.DefaultState;
import dev.jlkesh.java_telegram_bots.state.GenerateDataState;
import dev.jlkesh.java_telegram_bots.state.RegistrationState;
import dev.jlkesh.java_telegram_bots.state.State;
import lombok.NonNull;

import java.math.BigDecimal;

import static dev.jlkesh.java_telegram_bots.config.ThreadSafeBeansContainer.*;


/*@Slf4j*/
public class MessageHandler implements Handler {
    private final TelegramBot bot = TelegramBotConfiguration.get();

    @Override
    public void handle(Update update) {
        Message message = update.message();
        Chat chat = message.chat();
        Long chatID = chat.id();
        State state = userState.get(chatID);
        if ( state == null ) startRegister(chatID);
        else if ( state instanceof DefaultState defaultState )
            defaultMessageProcessor.get().process(update, defaultState);
        else if ( state instanceof RegistrationState registrationState )
            registrationMessageProcessor.get().process(update, registrationState);
        else if ( state instanceof GenerateDataState generateDataState )
            generateDataMessageProcessor.get().process(update, generateDataState);
    }

    private void startRegister(@NonNull Long chatID) {
        Dictionary<String, Object> pairs = new Dictionary<>(2);
        userState.put(chatID, RegistrationState.USERNAME);
        collected.put(chatID, pairs);
        SendMessage sendMessage = new SendMessage(chatID, "Welcome\nPlease Register\nUsername please");
        sendMessage.replyMarkup(new ForceReply());
        bot.execute(sendMessage);
    }
}
