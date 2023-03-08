package dev.jlkesh.java_telegram_bots.utils.factory;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import dev.jlkesh.java_telegram_bots.domains.History;
import dev.jlkesh.java_telegram_bots.faker.FieldType;
import dev.jlkesh.java_telegram_bots.faker.FileType;
import dev.jlkesh.java_telegram_bots.utils.BaseUtils;

import java.util.HashMap;
import java.util.Objects;

public class InlineKeyboardMarkupFactory {
    public static InlineKeyboardMarkup enterPasswordKeyboard() {
        InlineKeyboardMarkup replyMarkup = new InlineKeyboardMarkup();
        replyMarkup.addRow(
                getInlineButton(1, 1),
                getInlineButton(2, 2),
                getInlineButton(3, 3)
        );
        replyMarkup.addRow(
                getInlineButton(4, 4),
                getInlineButton(5, 5),
                getInlineButton(6, 6)
        );
        replyMarkup.addRow(
                getInlineButton(7, 7),
                getInlineButton(8, 8),
                getInlineButton(9, 9)
        );
        replyMarkup.addRow(
                getInlineButton(0, 0),
                getInlineButton(BaseUtils.TICK, "done"),
                getInlineButton(BaseUtils.CLEAR, "d")
        );
        return replyMarkup;
    }

    private static InlineKeyboardButton getInlineButton(final Object text, final Object callbackData) {
        var button = new InlineKeyboardButton(Objects.toString(text));
        button.callbackData(Objects.toString(callbackData));
        return button;
    }


    public static SendMessage getSendMessageWithPasswordKeyboard(Object chatID, String message) {
        SendMessage sendMessage = new SendMessage(chatID, message);
        sendMessage.replyMarkup(enterPasswordKeyboard());
        return sendMessage;
    }

    public static InlineKeyboardMarkup getFileTypeKeyboard() {
        return new InlineKeyboardMarkup(
                getInlineButton(FileType.JSON, "json"),
                getInlineButton(FileType.CSV, "csv"),
                getInlineButton(FileType.SQL, "sql")
        );
    }

    public static InlineKeyboardMarkup getFieldTypeButtons() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton[] rows = new InlineKeyboardButton[3];

        int i = 0;
        for (FieldType value : FieldType.values()) {
            rows[i++] = getInlineButton(value, value);
            if ((i % 3 == 0)) {
                inlineKeyboardMarkup.addRow(rows);
                rows = new InlineKeyboardButton[3];
                i = 0;
            }
        }


        return inlineKeyboardMarkup;
    }

    public static InlineKeyboardMarkup getConfirmOrCancelButton() {
        return new InlineKeyboardMarkup(
                getInlineButton("✅", "y"),
                getInlineButton("❌", "n")
        );
    }

    public static InlineKeyboardMarkup getSqlButtons() {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.addRow(
                getInlineButton("SERIAL", "SERIAL"),
                getInlineButton("INTEGER", "INTEGER"),
                getInlineButton("LONG", "LONG")
        );
        markup.addRow(
                getInlineButton("NUMERIC", "NUMERIC"),
                getInlineButton("VARCHAR", "VARCHAR"),
                getInlineButton("TEXT", "TEXT")
        );

        return markup;
    }

    public static InlineKeyboardMarkup getFileNumberButtons(HashMap<Object, History> files, Integer offset) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        int size = files.size();

        InlineKeyboardButton[] button;
        int buttonNumber;
        if (size <= 5) {
            buttonNumber = (5 - (5 - size));
            button = new InlineKeyboardButton[buttonNumber];
        } else {
            buttonNumber = (size - 5);
            button = new InlineKeyboardButton[5];
        }

        int i = 0;
        for (Object id : files.keySet()) {
            offset++;
            button[i++] = getInlineButton(offset, id);
            if (i == 5) {
                markup.addRow(button);
                button = new InlineKeyboardButton[buttonNumber];
                i = 0;
            }
        }
        if (size != 5 && size != 10) {
            markup.addRow(button);
        }
        markup.addRow(
                getInlineButton(BaseUtils.BACK, "b"),
                getInlineButton(BaseUtils.DELETE, "d"),
                getInlineButton(BaseUtils.NEXT, "n")
        );
        return markup;
    }
}
