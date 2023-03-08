package dev.jlkesh.java_telegram_bots.utils.factory;

import com.pengrad.telegrambot.model.request.Keyboard;
import com.pengrad.telegrambot.model.request.KeyboardButton;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import dev.jlkesh.java_telegram_bots.utils.MessageSourceUtils;

public class ReplyKeyboardMarkupFactory {
    public static ReplyKeyboardMarkup mainMenu(String language) {
        KeyboardButton generateDataButton = new KeyboardButton(MessageSourceUtils.getLocalizedMessage("main.menu.generate.data", language));
        KeyboardButton historyButton = new KeyboardButton(MessageSourceUtils.getLocalizedMessage("main.menu.history", language));
        KeyboardButton changeLanguageDataButton = new KeyboardButton(MessageSourceUtils.getLocalizedMessage("main.menu.change.language", language));
        KeyboardButton resetPasswordDataButton = new KeyboardButton(MessageSourceUtils.getLocalizedMessage("main.menu.reset.password", language));
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(
                new KeyboardButton[]{generateDataButton, historyButton},
                new KeyboardButton[]{changeLanguageDataButton, resetPasswordDataButton});
        replyKeyboardMarkup.selective(true);
        replyKeyboardMarkup.resizeKeyboard(true);
        return replyKeyboardMarkup;
    }

    public static ReplyKeyboardMarkup getMenuButton() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(new KeyboardButton("Menu"));
        replyKeyboardMarkup.selective(true);
        replyKeyboardMarkup.resizeKeyboard(true);
        return replyKeyboardMarkup;
    }
}
