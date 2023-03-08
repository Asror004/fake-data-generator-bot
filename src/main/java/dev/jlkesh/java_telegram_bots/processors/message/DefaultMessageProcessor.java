package dev.jlkesh.java_telegram_bots.processors.message;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ReplyKeyboardRemove;
import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.request.SendMessage;
import dev.jlkesh.java_telegram_bots.config.TelegramBotConfiguration;
import dev.jlkesh.java_telegram_bots.config.ThreadSafeBeansContainer;
import dev.jlkesh.java_telegram_bots.domains.History;
import dev.jlkesh.java_telegram_bots.dto.Dictionary;
import dev.jlkesh.java_telegram_bots.processors.Processor;
import dev.jlkesh.java_telegram_bots.state.DefaultState;
import dev.jlkesh.java_telegram_bots.state.GenerateDataState;
import dev.jlkesh.java_telegram_bots.utils.BaseUtils;
import dev.jlkesh.java_telegram_bots.utils.factory.AnswerCallbackQueryFactory;
import dev.jlkesh.java_telegram_bots.utils.factory.InlineKeyboardMarkupFactory;
import dev.jlkesh.java_telegram_bots.utils.factory.ReplyKeyboardMarkupFactory;
import dev.jlkesh.java_telegram_bots.utils.factory.SendMessageFactory;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

import static dev.jlkesh.java_telegram_bots.config.ThreadSafeBeansContainer.*;
import static dev.jlkesh.java_telegram_bots.utils.MessageSourceUtils.getLocalizedMessage;

public class DefaultMessageProcessor implements Processor<DefaultState> {
    private final TelegramBot bot = TelegramBotConfiguration.get();

    @Override
    public void process(Update update, DefaultState state) {
        Message message = update.message();
        User from = message.from();
        String text = message.text();
        Long chatID = message.chat().id();
        String language = message.from().languageCode();
        if (state.equals(DefaultState.DELETE)) {
            bot.execute(new DeleteMessage(chatID, message.messageId()));
        } else if (state.equals(DefaultState.MAIN_STATE)) {
            collected.put(chatID, new Dictionary<>(1));
            collected.get(chatID).put("language", language);
            if (text.equals(getLocalizedMessage("main.menu.generate.data", language))) {
                // TODO: 05/02/23 localize
                SendMessage sendMessage = new SendMessage(chatID, "Enter file Name");
                sendMessage.replyMarkup(ReplyKeyboardMarkupFactory.getMenuButton());
                bot.execute(sendMessage);
                userState.put(chatID, GenerateDataState.FILE_NAME);

            } else if (text.equals(getLocalizedMessage("main.menu.history", language))) {
                int offset = 0;
                ThreadSafeBeansContainer.offset.put(chatID, offset);
                HashMap<Object, History> files = userService.get().getUserFiles(offset, chatID).getBody();

                if (Objects.isNull(files)) {
                    // TODO: 07/02/23 localized
                    bot.execute(new SendMessage(chatID, "You have no files "+BaseUtils.NO_FILE));
                } else {

                    // TODO: 07/02/23 localized
                    SendMessage filesMessage = new SendMessage(chatID, "Show files");
                    filesMessage.replyMarkup(new ReplyKeyboardRemove());
                    bot.execute(filesMessage);

                    userFiles.put(chatID, files);
                    String messageText = getMessageText(chatID, offset);
                    SendMessage sendMessage = new SendMessage(chatID, messageText);
                    sendMessage.parseMode(ParseMode.HTML);
                    sendMessage.replyMarkup(InlineKeyboardMarkupFactory.getFileNumberButtons(files, offset));
                    bot.execute(sendMessage);

                }
            } else {
                bot.execute(SendMessageFactory.sendMessageWithMainMenu(chatID, ("Language "+from.languageCode()), from.languageCode()));
            }
        }else {
            bot.execute(new DeleteMessage(chatID, message.messageId()));
        }
    }

    public String getMessageText(Long chatId, int offset){
        HashMap<Object, History> files = userFiles.get(chatId);

        StringBuilder sb = new StringBuilder();

        for (History history : files.values()) {
            sb.append("<b>");
            sb.append(++offset);
            sb.append("</b>. File: ");
            sb.append(history.getFileName());
            sb.append(",  Time: ");
            sb.append(history.getCreatedAt().format(BaseUtils.formatter));
            sb.append("\n");
        }
        userState.put(chatId, DefaultState.HISTORY);
        return sb.toString();
    }
}
