package dev.jlkesh.java_telegram_bots.faker;

import com.github.javafaker.*;
import com.github.javafaker.service.RandomService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiFunction;


public class FakerApplicationService {

    private static final Scanner scanner = new Scanner(System.in);
    private static final AtomicLong id = new AtomicLong(1);
    private static final Faker faker = new Faker();
    private static final Country country = faker.country();
    private static final Address address = faker.address();
    private static final Book book = faker.book();
    private static final Name name = faker.name();
    private static final Lorem lorem = faker.lorem();
    private static final RandomService random = faker.random();
    private static final PhoneNumber phoneNumber = faker.phoneNumber();
    public static final Map<FieldType, BiFunction<Integer, Integer, Object>> functions = new HashMap<>() {{
        put(FieldType.ID, (a, b) -> id.incrementAndGet());
        put(FieldType.UUID, (a, b) -> UUID.randomUUID());
        put(FieldType.BOOK_TITLE, (a, b) -> book.title());
        put(FieldType.BOOK_AUTHOR, (a, b) -> book.author());
        put(FieldType.POST_TITLE, (a, b) -> String.join(" ", lorem.words(random.nextInt(a, b))));
        put(FieldType.POST_BODY, (a, b) -> String.join("", lorem.paragraphs(random.nextInt(a, b))));
        put(FieldType.FIRSTNAME, (a, b) -> name.firstName());
        put(FieldType.LASTNAME, (a, b) -> name.lastName());
        put(FieldType.USERNAME, (a, b) -> name.username());
        put(FieldType.FULLNAME, (a, b) -> name.fullName());
        put(FieldType.BLOOD_GROUP, (a, b) -> name.bloodGroup());
        put(FieldType.EMAIL, (a, b) -> name.username() + "@" + ( random.nextBoolean() ? "gmail.com" : "mail.ru" ));
        put(FieldType.GENDER, (a, b) -> random.nextBoolean() ? "MALE" : "FEMALE");
        put(FieldType.PHONE, (a, b) -> phoneNumber.cellPhone());
        put(FieldType.LOCAlDATE, (a, b) -> {
            int year = random.nextInt(a, b - 1);
            int month = random.nextInt(1, 12);
            YearMonth yearMonth = YearMonth.of(year, month);
            int day = random.nextInt(1, yearMonth.getMonth().length(yearMonth.isLeapYear()));
            return LocalDate.of(year, month, day);
        });
        put(FieldType.COUNTRY_CODE, (a, b) -> country.countryCode3());
        put(FieldType.COUNTRY_ZIP_CODE, (a, b) -> address.zipCode());
        put(FieldType.CAPITAL, (a, b) -> country.capital());
        put(FieldType.WORD, (a, b) -> lorem.word());
        put(FieldType.WORDS, (a, b) -> lorem.words(random.nextInt(a, b)));
        put(FieldType.PARAGRAPH, (a, b) -> lorem.paragraph());
        put(FieldType.PARAGRAPHS, (a, b) -> lorem.paragraphs(random.nextInt(a, b)));
        put(FieldType.AGE, random :: nextInt);
        put(FieldType.RANDOM_INT, random::nextInt);
        put(FieldType.LETTERS, (a, b) -> lorem.characters(a, b, true));
    }};

    public static final List<FieldType> BLACK_LIST = List.of(
            FieldType.AGE, FieldType.WORDS, FieldType.PARAGRAPHS, FieldType.RANDOM_INT, FieldType.POST_TITLE, FieldType.POST_BODY, FieldType.LETTERS
    );

    public String processRequest(FakerApplicationGenerateRequest fakerApplicationGenerateRequest) {
        var fileType = fakerApplicationGenerateRequest.getFileType();
        var fileName = fakerApplicationGenerateRequest.getFileName() + "." + fileType.name().toLowerCase();
        var rowsCount = fakerApplicationGenerateRequest.getCount();
        var fields = fakerApplicationGenerateRequest.getFields();
        return switch ( fileType ) {
            case JSON -> generateDataAsJson(rowsCount, fileName, fields);
            case CSV -> generateDataAsCsv(rowsCount, fileName, fields);
            case SQL -> generateDataAsSql(rowsCount, fileName, fields);
        };
    }

    private String generateDataAsSql(int rowsCount, String fileName, Set<Field> fields) {
        synchronized (FakerApplicationService.class){
            var result = new StringJoiner("\n");
            var createAndInsertTable = new StringBuilder("create table ");

            createAndInsertTable.append(fileName, 0, fileName.length()-4);
            createAndInsertTable.append("(\n");
            for (Field field : fields) {
                createAndInsertTable.append(field.getFieldName());
                createAndInsertTable.append(" ");
                createAndInsertTable.append(field.getSqlVariableType().name());
                createAndInsertTable.append(" ,\n");
            }
            createAndInsertTable.deleteCharAt(createAndInsertTable.lastIndexOf(","));
            createAndInsertTable.append(");");

            result.add(createAndInsertTable.toString());
            createAndInsertTable = new StringBuilder();
            createAndInsertTable.append("insert into ");
            createAndInsertTable.append(fileName, 0, fileName.length()-4);
            createAndInsertTable.append(" values ");


            for ( int i = 0; i < rowsCount; i++ ) {
                var row = new StringJoiner(", ", "(", "); ");
                for ( Field field : fields )
                    row.add(field.getPatternAsSql());
                result.add(createAndInsertTable+row.toString());
            }

            Path path = generateFile(fileName, result.toString().substring(0, result.length() - 2));
            return path.toAbsolutePath().toString();
        }
    }

    private String generateDataAsCsv(int rowsCount, String fileName, Set<Field> fields) {
        synchronized (FakerApplicationService.class){
            var result = new StringJoiner("\n");
            var header = new StringJoiner(", ");
            fields.forEach(field -> header.add(field.getFieldName()));
            result.add(header.toString());
            for ( int i = 0; i < rowsCount; i++ ) {
                var row = new StringJoiner(", ");
                for ( Field field : fields )
                    row.add(field.getPatternAsCsv());
                result.add(row.toString());
            }
            Path path = generateFile(fileName, result.toString());
            return path.toAbsolutePath().toString();
        }
    }

    private Path generateFile(String fileName, String result){
        Path path = Path.of(fileName);
        try {
            if ( Files.notExists(path) )
                Files.createFile(path);
            Files.writeString(path, result, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return path;
    }

    private String generateDataAsJson(int rowsCount, String fileName, Set<Field> fields) {
        synchronized (FakerApplicationService.class){
            var result = new StringJoiner(",\n", "[", "]");
            for ( int i = 0; i < rowsCount; i++ ) {
                var row = new StringJoiner(", ", "{", "}");
                for ( Field field : fields )
                    row.add(field.getPatternAsJson());
                result.add(row.toString());
            }
            Path path = generateFile(fileName, result.toString());
            return path.toAbsolutePath().toString();
        }
    }

}
