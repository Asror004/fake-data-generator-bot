package dev.jlkesh.java_telegram_bots.processors.callback;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.request.SendMessage;
import dev.jlkesh.java_telegram_bots.config.TelegramBotConfiguration;
import dev.jlkesh.java_telegram_bots.domains.UserDomain;
import dev.jlkesh.java_telegram_bots.dto.Dictionary;
import dev.jlkesh.java_telegram_bots.dto.Response;
import dev.jlkesh.java_telegram_bots.processors.Processor;
import dev.jlkesh.java_telegram_bots.state.DefaultState;
import dev.jlkesh.java_telegram_bots.state.RegistrationState;
import dev.jlkesh.java_telegram_bots.utils.factory.AnswerCallbackQueryFactory;
import dev.jlkesh.java_telegram_bots.utils.BaseUtils;
import dev.jlkesh.java_telegram_bots.utils.factory.SendMessageFactory;

import static dev.jlkesh.java_telegram_bots.config.ThreadSafeBeansContainer.*;

public class RegisterUserCallbackProcessor implements Processor<RegistrationState> {
    private final TelegramBot bot = TelegramBotConfiguration.get();

    @Override
    public void process(Update update, RegistrationState state) {
        CallbackQuery callbackQuery = update.callbackQuery();
        Message message = callbackQuery.message();
        int messageId = message.messageId();
        Chat chat = callbackQuery.message().chat();
        Long chatID = chat.id();
        String data = callbackQuery.data();

        Dictionary<String, Object> dictionary = collected.get(chatID);
        String password = (String) dictionary.get("password", "");
        String language = (String) dictionary.get("language", "en");
        if ( data.equals("done") ) {
            if ( password.length() == 4 ) {
                UserDomain domain = UserDomain.builder()
                        .chatID(chatID)
                        .username((String) dictionary.get("username"))
                        .password(password)
                        .firstName(message.chat().firstName())
                        .language((String) dictionary.get("language"))
                        .build();
                Response<Void> response = userService.get().create(domain);
                if ( response.isSuccess() ) {
                    userState.put(chatID, DefaultState.MAIN_STATE);
                    bot.execute(SendMessageFactory.sendMessageWithMainMenu(chatID, "Menu", language));
                    bot.execute(new DeleteMessage(chatID, messageId));
                } else {
                    bot.execute(new SendMessage(chatID, response.getFriendlyErrorMessage()));
                    userState.remove(chatID);
                    collected.remove(chatID);
                }
            } else
                bot.execute(AnswerCallbackQueryFactory.answerCallbackQuery(callbackQuery.id(), "Password must contain four numbers"));
        } else if ( data.equals("d") ) {
            int length = password.length();
            if ( length == 0 )
                bot.execute(AnswerCallbackQueryFactory.answerCallbackQuery(callbackQuery.id(), "Password field is empty"));
            else {
                String messageText = "Enter Password Please \n _ _ _ _ ";
                bot.execute(SendMessageFactory.getEditMessageTextForPassword(chatID, messageId, messageText));
                dictionary.put("password", "");
            }
        } else {
            int length = password.length();
            if ( length < 4 ) {
                length++;
                dictionary.put("password", password + data);
                String messageText = "Enter Password Please \n " + BaseUtils.STAR.repeat(length) + " _ ".repeat(4 - length);
                bot.execute(SendMessageFactory.getEditMessageTextForPassword(chatID, messageId, messageText));
            } else {
                String messageText = "Password field is full!\nPlease press button " + BaseUtils.TICK;
                bot.execute(AnswerCallbackQueryFactory.answerCallbackQuery(callbackQuery.id(), messageText));
            }
        }
    }

}
