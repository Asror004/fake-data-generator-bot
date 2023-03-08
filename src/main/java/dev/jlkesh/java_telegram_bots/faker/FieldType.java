package dev.jlkesh.java_telegram_bots.faker;

public enum FieldType {
    ID(""),
    UUID("\""),
    BOOK_TITLE("\""),
    BOOK_AUTHOR("\""),
    POST_TITLE("\""),
    POST_BODY("\""),
    FIRSTNAME("\""),
    LASTNAME("\""),
    USERNAME("\""),
    FULLNAME("\""),
    BLOOD_GROUP("\""),
    EMAIL("\""),
    GENDER("\""),
    PHONE("\""),
    LOCAlDATE("\""),
    AGE(""),
    COUNTRY_CODE("\""),
    COUNTRY_ZIP_CODE("\""),
    CAPITAL("\""),
    WORD("\""),
    WORDS("\""),
    PARAGRAPH("\""),
    PARAGRAPHS("\""),
    LETTERS("\""),
    RANDOM_INT("");

    private final String i;

    FieldType(String i) {
        this.i = i;
    }
    public String getRowAsCsv(Object data) {
        return String.valueOf(data);
    }


    public String getRowAsJson(String fieldName, Object data) {
        return ( "\"" + fieldName + "\" : " + i + data + i );
    }

}