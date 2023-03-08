package dev.jlkesh.java_telegram_bots.utils.factory;

import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.request.SendMessage;

import static dev.jlkesh.java_telegram_bots.utils.MessageSourceUtils.getLocalizedMessage;

public class SendMessageFactory {
    public static EditMessageText getEditMessageTextForPassword(Object chatID, int messageID, String messageText) {
        EditMessageText editMessageText = new EditMessageText(chatID, messageID, messageText);
        editMessageText.replyMarkup(InlineKeyboardMarkupFactory.enterPasswordKeyboard());
        return editMessageText;
    }

    public static SendMessage sendMessageWithMainMenu(Object chatID, String messageText, String language) {
        SendMessage sendMessage = new SendMessage(chatID, messageText);
        sendMessage.replyMarkup(ReplyKeyboardMarkupFactory.mainMenu(language));
        return sendMessage;
    }


    public static SendMessage getSendMessageWithFileTypeKeyboard(Long chatID, String key, String language) {
        SendMessage sendMessage = new SendMessage(chatID, getLocalizedMessage(key, language));
        sendMessage.replyMarkup(InlineKeyboardMarkupFactory.getFileTypeKeyboard());
        return sendMessage;
    }
}
