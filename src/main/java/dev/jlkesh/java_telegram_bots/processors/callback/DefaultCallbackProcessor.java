package dev.jlkesh.java_telegram_bots.processors.callback;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.request.SendDocument;
import com.pengrad.telegrambot.request.SendMessage;
import dev.jlkesh.java_telegram_bots.config.TelegramBotConfiguration;
import dev.jlkesh.java_telegram_bots.config.ThreadSafeBeansContainer;
import dev.jlkesh.java_telegram_bots.domains.History;
import dev.jlkesh.java_telegram_bots.domains.UserDomain;
import dev.jlkesh.java_telegram_bots.dto.Dictionary;
import dev.jlkesh.java_telegram_bots.dto.Response;
import dev.jlkesh.java_telegram_bots.processors.Processor;
import dev.jlkesh.java_telegram_bots.state.DefaultState;
import dev.jlkesh.java_telegram_bots.state.RegistrationState;
import dev.jlkesh.java_telegram_bots.utils.BaseUtils;
import dev.jlkesh.java_telegram_bots.utils.MessageSourceUtils;
import dev.jlkesh.java_telegram_bots.utils.factory.AnswerCallbackQueryFactory;
import dev.jlkesh.java_telegram_bots.utils.factory.InlineKeyboardMarkupFactory;
import dev.jlkesh.java_telegram_bots.utils.factory.SendMessageFactory;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Objects;

import static dev.jlkesh.java_telegram_bots.config.ThreadSafeBeansContainer.*;

public class DefaultCallbackProcessor implements Processor<DefaultState> {
    private final TelegramBot bot = TelegramBotConfiguration.get();

    @Override
    public void process(Update update, DefaultState state) {
        CallbackQuery callbackQuery = update.callbackQuery();
        Message message = callbackQuery.message();
        int messageId = message.messageId();
        Chat chat = callbackQuery.message().chat();
        Long chatID = chat.id();
        String data = callbackQuery.data();

        if (state.equals(DefaultState.HISTORY)) {
            Integer offset = ThreadSafeBeansContainer.offset.get(chatID);

            if (data.equals("n")) {
                offset += 10;
                HashMap<Object, History> files = userService.get().getUserFiles(offset, chatID).getBody();

                if (Objects.isNull(files)) {
                    // TODO: 07/02/23 localized
                    bot.execute(AnswerCallbackQueryFactory.answerCallbackQuery(callbackQuery.id(), "You have not other files"));
                } else {
                    sendFilesNameMessage(message, offset, files);
                }
            } else if (data.equals("b")) {
                if (offset < 10) {
                    bot.execute(AnswerCallbackQueryFactory.answerCallbackQuery(callbackQuery.id(),
                            "There are no files back page \nPlease click the %s button".formatted(BaseUtils.NEXT)));
                } else {
                    offset -= 10;
                    HashMap<Object, History> files = userService.get().getUserFiles(offset, chatID).getBody();

                    sendFilesNameMessage(message, offset, files);
                }

            } else if (data.equals("d")) {
                String language = String.valueOf(collected.get(chatID).get("language"));
                bot.execute(SendMessageFactory.sendMessageWithMainMenu(chatID, "Menu", language));
                bot.execute(new DeleteMessage(chatID, messageId));
                userState.put(chatID, DefaultState.MAIN_STATE);
            } else {
                History history = userFiles.get(chatID).get(Integer.parseInt(data));

                StringBuilder sb = new StringBuilder();
                sb.append("File name: ");
                sb.append(history.getFileName());
                sb.append("\nFile size: ");
                sb.append(history.getSize());
                sb.append("\nRow count: ");
                sb.append(history.getRowCount());
                sb.append("\nField count: ");
                sb.append(history.getFieldCount());
                sb.append("\nTime: ");
                sb.append(history.getCreatedAt().format(BaseUtils.formatter));

                SendDocument sendDocument = new SendDocument(chatID, history.getFileId());
                sendDocument.caption(sb.toString());
                bot.execute(sendDocument);
            }
        }
    }

    private void sendFilesNameMessage(Message message, int offset, HashMap<Object, History> files) {
        Long chatID = message.chat().id();
        ThreadSafeBeansContainer.offset.put(chatID, offset);
        userFiles.put(chatID, files);
        String messageText = defaultMessageProcessor.get().getMessageText(chatID, offset);
        EditMessageText editMessageText = new EditMessageText(chatID, message.messageId(), messageText);
        editMessageText.parseMode(ParseMode.HTML);
        editMessageText.replyMarkup(InlineKeyboardMarkupFactory.getFileNumberButtons(files, offset));
        bot.execute(editMessageText);
    }
}
