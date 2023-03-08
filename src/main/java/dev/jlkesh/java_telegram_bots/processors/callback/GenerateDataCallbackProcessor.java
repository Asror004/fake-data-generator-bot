package dev.jlkesh.java_telegram_bots.processors.callback;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.*;
import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.request.SendDocument;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import dev.jlkesh.java_telegram_bots.config.TelegramBotConfiguration;
import dev.jlkesh.java_telegram_bots.dto.Dictionary;
import dev.jlkesh.java_telegram_bots.faker.*;
import dev.jlkesh.java_telegram_bots.processors.Processor;
import dev.jlkesh.java_telegram_bots.state.DefaultState;
import dev.jlkesh.java_telegram_bots.state.GenerateDataState;
import dev.jlkesh.java_telegram_bots.utils.factory.AnswerCallbackQueryFactory;
import dev.jlkesh.java_telegram_bots.utils.factory.InlineKeyboardMarkupFactory;
import dev.jlkesh.java_telegram_bots.utils.factory.ReplyKeyboardMarkupFactory;
import dev.jlkesh.java_telegram_bots.utils.factory.SendMessageFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static dev.jlkesh.java_telegram_bots.config.ThreadSafeBeansContainer.*;
import static dev.jlkesh.java_telegram_bots.state.GenerateDataState.MIN;
import static dev.jlkesh.java_telegram_bots.state.GenerateDataState.SQL_TYPE;
import static dev.jlkesh.java_telegram_bots.utils.MessageSourceUtils.getLocalizedMessage;

public class GenerateDataCallbackProcessor implements Processor<GenerateDataState> {
    private final TelegramBot bot = TelegramBotConfiguration.get();

    @Override
    public void process(Update update, GenerateDataState state) {
        CallbackQuery callbackQuery = update.callbackQuery();
        Message message = callbackQuery.message();
        String callbackData = callbackQuery.data();
        Chat chat = message.chat();
        Long chatID = chat.id();
        FakerApplicationGenerateRequest faker = fakerApplicationGenerateRequest.get(chatID);
        if (state.equals(GenerateDataState.FILE_TYPE)) {

            bot.execute(new SendMessage(chatID, "Enter rows Count "));
            bot.execute(new DeleteMessage(chatID, message.messageId()));
            userState.put(chatID, GenerateDataState.ROW_COUNT);

            if (callbackData.equals("json")) {
                faker.setFileType(FileType.JSON);
            } else if (callbackData.equals("csv")) {
                faker.setFileType(FileType.CSV);
            } else if (callbackData.equals("sql")) {
                faker.setFileType(FileType.SQL);
            }

        } else if (state.equals(GenerateDataState.FIELDS)) {
            Field field = fieldMap.get(chatID);
            field.setFieldType(FieldType.valueOf(callbackData));
            bot.execute(new DeleteMessage(chatID, message.messageId()));

            switch (callbackData) {
                case "AGE" -> {
                    bot.execute(new SendMessage(chatID, "Enter AGE min value"));
                    userState.put(chatID, MIN);
                }
                case "POST_TITLE" -> {
                    bot.execute(new SendMessage(chatID, "Enter POST_TITLE words min value"));
                    userState.put(chatID, MIN);
                }
                case "POST_BODY" -> {
                    bot.execute(new SendMessage(chatID, "Enter POST_BODY paragraph min value"));
                    userState.put(chatID, MIN);
                }
                case "LOCAlDATE" -> {
                    bot.execute(new SendMessage(chatID, "Enter LOCAlDATE year min value"));
                    userState.put(chatID, MIN);
                }
                case "PARAGRAPHS" -> {
                    bot.execute(new SendMessage(chatID, "Enter PARAGRAPHS min value"));
                    userState.put(chatID, MIN);
                }
                case "RANDOM_INT" -> {
                    bot.execute(new SendMessage(chatID, "Enter RANDOM_INT min value"));
                    userState.put(chatID, MIN);
                }
                case "WORDS" -> {
                    bot.execute(new SendMessage(chatID, "Enter WORDS min value"));
                    userState.put(chatID, MIN);
                }
                case "LETTERS" -> {
                    bot.execute(new SendMessage(chatID, "Enter LETTERS min value"));
                    userState.put(chatID, MIN);
                }
                default -> {
                    SendMessage sendMessage = new SendMessage(chatID, "Add another field");
                    sendMessage.replyMarkup(InlineKeyboardMarkupFactory.getConfirmOrCancelButton());
                    bot.execute(sendMessage);
                    userState.put(chatID, GenerateDataState.ADD_FIELD);
                    faker.getFields().add(field);
                }
            }
        }
        else if ( state.equals(SQL_TYPE) ) {
            SendMessage sendMessage = new SendMessage(chatID, "Select field type");
            sendMessage.replyMarkup(InlineKeyboardMarkupFactory.getFieldTypeButtons());
            bot.execute(sendMessage);
            bot.execute(new DeleteMessage(chatID, message.messageId()));
            fieldMap.get(chatID).setSqlVariableType(SqlVariableType.valueOf(callbackData));
            userState.put(chatID, GenerateDataState.FIELDS);
        }
        else if (state.equals(GenerateDataState.ADD_FIELD)) {
            bot.execute(new DeleteMessage(chatID, message.messageId()));
            if (callbackData.equals("y")) {
                bot.execute(new SendMessage(chatID, "Enter field name"));
                userState.put(chatID, GenerateDataState.FIELDS);
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append("File name: ");
                sb.append(faker.getFileName());
                sb.append(".");
                sb.append(faker.getFileType().toString().toLowerCase());
                sb.append("\nFile type: ");
                sb.append(faker.getFileType());
                sb.append("\nRow count: ");
                sb.append(faker.getCount());
                sb.append("\n\nFields: ");
                for (Field field : faker.getFields()) {
                    sb.append("\nName: ");
                    sb.append(field.getFieldName());
                    if ( faker.getFileType().equals(FileType.SQL) ) {
                        sb.append(",  Variable type: ");
                        sb.append(field.getSqlVariableType().name());
                    }
                    sb.append(",  Type: ");
                    sb.append(field.getFieldType());
                }

                bot.execute(new SendMessage(chatID, sb.toString()));
                SendMessage sendMessage = new SendMessage(chatID, "Confirm");
                sendMessage.replyMarkup(InlineKeyboardMarkupFactory.getConfirmOrCancelButton());
                bot.execute(sendMessage);

                userState.put(chatID, GenerateDataState.GENERATE);
            }
        } else if (state.equals(GenerateDataState.GENERATE)) {
            String language = String.valueOf(collected.get(chatID).get("language"));
            if (callbackData.equals("y")) {
                FakerApplicationService service = new FakerApplicationService();
                String path = service.processRequest(faker);
                File file = new File(path);

                SendDocument sendDocument = new SendDocument(chatID, file);
                sendDocument.replyMarkup(ReplyKeyboardMarkupFactory.mainMenu(language));

                SendResponse execute = bot.execute(sendDocument);
                Message eMessage = execute.message();

                userService.get().addHistory(eMessage, faker);

                try {
                    Files.deleteIfExists(Path.of(path));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                SendMessage sendMessage = new SendMessage(chatID, "Menu");
                sendMessage.replyMarkup(ReplyKeyboardMarkupFactory.mainMenu(language));
                bot.execute(sendMessage);
            }
            bot.execute(new DeleteMessage(chatID, message.messageId()));
            userState.put(chatID, DefaultState.MAIN_STATE);
        }

    }
}
