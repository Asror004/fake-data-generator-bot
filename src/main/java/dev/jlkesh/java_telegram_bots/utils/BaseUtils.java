package dev.jlkesh.java_telegram_bots.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.format.DateTimeFormatter;

public class BaseUtils {
    public static final String STAR = "*️⃣";
    public static final String CLEAR = "\uD83C\uDD91";
    public static final String TICK = "✅";
    public static final String GENERATE = "\uD83C\uDFB2";
    public static final String LANGUAGE = "\uD83C\uDF0D";
    public static final String HISTORY = "\uD83D\uDDD2";
    public static final String KEY = "\uD83D\uDD11";
    public static final String BACK = "⬅️";
    public static final String NEXT = "➡️";
    public static final String DELETE = "❌";
    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm  dd.MM.yyyy" );
    public static final String NO_FILE = "\uD83D\uDE14";

    public static String getStackStraceAsString(Throwable e) {
        StringWriter out = new StringWriter();
        e.printStackTrace(new PrintWriter(out));
        return out.toString();
    }
}
