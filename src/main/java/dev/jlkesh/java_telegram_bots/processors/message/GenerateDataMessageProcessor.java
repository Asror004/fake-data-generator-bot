package dev.jlkesh.java_telegram_bots.processors.message;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import dev.jlkesh.java_telegram_bots.config.TelegramBotConfiguration;
import dev.jlkesh.java_telegram_bots.faker.FakerApplicationGenerateRequest;
import dev.jlkesh.java_telegram_bots.faker.Field;
import dev.jlkesh.java_telegram_bots.faker.FileType;
import dev.jlkesh.java_telegram_bots.processors.Processor;
import dev.jlkesh.java_telegram_bots.state.DefaultState;
import dev.jlkesh.java_telegram_bots.state.GenerateDataState;
import dev.jlkesh.java_telegram_bots.utils.factory.InlineKeyboardMarkupFactory;
import dev.jlkesh.java_telegram_bots.utils.factory.ReplyKeyboardMarkupFactory;
import dev.jlkesh.java_telegram_bots.utils.factory.SendMessageFactory;

import static dev.jlkesh.java_telegram_bots.config.ThreadSafeBeansContainer.*;
import static dev.jlkesh.java_telegram_bots.state.GenerateDataState.SQL_TYPE;

public class GenerateDataMessageProcessor implements Processor<GenerateDataState> {
    private final TelegramBot bot = TelegramBotConfiguration.get();

    @Override
    public void process(Update update, GenerateDataState state) {
        Message message = update.message();
        String text = message.text();
        Long chatID = message.chat().id();
        String language = message.from().languageCode();

        FakerApplicationGenerateRequest faker = fakerApplicationGenerateRequest.get(chatID);
        if (text.equals("Menu")) {
            SendMessage sendMessage = new SendMessage(chatID, "Menu");
            sendMessage.replyMarkup(ReplyKeyboardMarkupFactory.mainMenu(language));
            bot.execute(sendMessage);
            userState.put(chatID, DefaultState.MAIN_STATE);
        } else if (state.equals(GenerateDataState.FILE_NAME)) {
            bot.execute(SendMessageFactory.getSendMessageWithFileTypeKeyboard(chatID, "main.menu.file.type", language));
            FakerApplicationGenerateRequest request = new FakerApplicationGenerateRequest();
            request.setFileName(text);
            fakerApplicationGenerateRequest.put(chatID, request);
            userState.put(chatID, GenerateDataState.FILE_TYPE);
        } else if (state.equals(GenerateDataState.ROW_COUNT)) {
            int count;
            try {
                count = Integer.parseInt(text);
            } catch (Exception e) {
                bot.execute(new SendMessage(chatID, "You must enter a number"));
                return;
            }

            if (count < 1 || count > 2000)
                bot.execute(new SendMessage(chatID, "Count 1 - 2000"));
            else {

                bot.execute(new SendMessage(chatID, "Enter field name"));
                userState.put(chatID, GenerateDataState.FIELDS);
                faker.setCount(count);
            }
        } else if (state.equals(GenerateDataState.FIELDS)) {
            // TODO: 05/02/23 Localize

            if (faker.getFileType().equals(FileType.SQL)) {
                SendMessage sendMessage = new SendMessage(chatID, "Enter variable type");
                sendMessage.replyMarkup(InlineKeyboardMarkupFactory.getSqlButtons());
                bot.execute(sendMessage);
                userState.put(chatID, SQL_TYPE);
            }else {
                SendMessage sendMessage = new SendMessage(chatID, "Select field type");
                sendMessage.replyMarkup(InlineKeyboardMarkupFactory.getFieldTypeButtons());
                bot.execute(sendMessage);
            }

            Field field = new Field();
            field.setFieldName(text);
            fieldMap.put(chatID, field);
        } else if (state.equals(GenerateDataState.MIN)) {
            try {
                Field field = fieldMap.get(chatID);
                String fieldName = field.getFieldName();
                bot.execute(new SendMessage(chatID, "Enter "+fieldName+" max value"));
                field.setMin(Integer.parseInt(text));
                userState.put(chatID, GenerateDataState.MAX);
            } catch (Exception e) {
                bot.execute(new SendMessage(chatID, "Enter number"));
            }

        } else if (state.equals(GenerateDataState.MAX)) {
            try {

                int max = Integer.parseInt(text);

                Field field = fieldMap.get(chatID);
                if ( max <= field.getMin() ) {
                    bot.execute(new SendMessage(chatID, "Enter number > "+field.getMin()));
                }else {
                    SendMessage sendMessage = new SendMessage(chatID, "Add another field");
                    sendMessage.replyMarkup(InlineKeyboardMarkupFactory.getConfirmOrCancelButton());
                    bot.execute(sendMessage);

                    userState.put(chatID, GenerateDataState.ADD_FIELD);
                    field.setMax(Integer.parseInt(text));
                    fakerApplicationGenerateRequest.get(chatID).getFields().add(field);
                }

            } catch (Exception e) {
                bot.execute(new SendMessage(chatID, "Enter number"));
            }
        }
    }
}
