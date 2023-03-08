package dev.jlkesh.java_telegram_bots.state;

public enum GenerateDataState implements State {
    FILE_NAME,
    FILE_TYPE,
    ROW_COUNT,
    FIELDS,
    SQL_TYPE,
    MIN,
    MAX,
    ADD_FIELD,
    GENERATE
}
