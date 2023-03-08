package dev.jlkesh.java_telegram_bots.faker;

public enum SqlVariableType {
    SERIAL(""),
    INTEGER(""),
    LONG(""),
    NUMERIC(""),
    VARCHAR("'"),
    TEXT("'");

    private String i;
    public String getRowAsSql(Object data) {
        return (i+String.valueOf(data).replace("'","''")+i);
    }

    SqlVariableType(String i) {
        this.i = i;
    }
}
