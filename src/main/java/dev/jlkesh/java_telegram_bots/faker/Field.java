package dev.jlkesh.java_telegram_bots.faker;

import lombok.*;

import java.util.function.BiFunction;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = {"fieldName", "fieldType"})
public class Field {
    private String fieldName;
    private FieldType fieldType;
    private BiFunction<Integer, Integer, Object> func;
    private SqlVariableType sqlVariableType;
    private int min;
    private int max;

    public Field(String fieldName, FieldType fieldType, int min, int max) {
        this.fieldName = fieldName;
        this.fieldType = fieldType;
        this.func = FakerApplicationService.functions.get(fieldType);
        this.min = min;
        this.max = max;
    }

    public String getPatternAsJson() {
        return fieldType.getRowAsJson(fieldName, func.apply(min, max));
    }
    public String getPatternAsCsv() {
        return fieldType.getRowAsCsv(func.apply(min, max));
    }
    public String getPatternAsSql() {
        return sqlVariableType.getRowAsSql(func.apply(min, max));
    }


    public void setFieldType(FieldType fieldType) {
        this.func = FakerApplicationService.functions.get(fieldType);
        this.fieldType = fieldType;
    }

    @Override
    public String toString() {
        return "\033[1;92m%s : %s \033[0m\n".formatted(fieldName, fieldType.name());
    }
}
