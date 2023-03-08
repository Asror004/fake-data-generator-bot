package dev.jlkesh.java_telegram_bots.processors.message;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ForceReply;
import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.request.SendMessage;
import dev.jlkesh.java_telegram_bots.config.TelegramBotConfiguration;
import dev.jlkesh.java_telegram_bots.processors.Processor;
import dev.jlkesh.java_telegram_bots.state.RegistrationState;
import dev.jlkesh.java_telegram_bots.utils.factory.InlineKeyboardMarkupFactory;

import static dev.jlkesh.java_telegram_bots.config.ThreadSafeBeansContainer.*;

public class RegistrationMessageProcessor implements Processor<RegistrationState> {
    private final TelegramBot bot = TelegramBotConfiguration.get();

    @Override
    public void process(Update update, RegistrationState state) {
        // TODO: 05/02/23 Localize
        Message message = update.message();
        String username = message.text();
        Long chatID = message.chat().id();
        if ( state.equals(RegistrationState.USERNAME) ) {

            if ( userService.get().hasUsername(username).getBody() ) {
                // TODO: 06/02/23 localized
                SendMessage sendMessage = new SendMessage(chatID, "Username already exists\nEnter another username");
                sendMessage.replyMarkup(new ForceReply());
                bot.execute(sendMessage);
            }else {
                String text = "Enter Password Please \n _ _ _ _ ";
                bot.execute(InlineKeyboardMarkupFactory.getSendMessageWithPasswordKeyboard(chatID, text));
                userState.put(chatID, RegistrationState.PASSWORD);
                collected.get(chatID).put("username", username);
                collected.get(chatID).put("language", message.from().languageCode());
            }
        } else if ( state.equals(RegistrationState.PASSWORD) ) {
            bot.execute(new DeleteMessage(chatID, message.messageId()));
        }
    }
}
