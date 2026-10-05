package com.nhom12.hospital.repository;

import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

final class EntityProcedureSupport {

    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

    private EntityProcedureSupport() {
    }

    static void save(JdbcTemplate jdbcTemplate, String entityName, Map<String, ?> columns) {
        jdbcTemplate.update(
                "EXEC dbo.usp_Entity_Save @EntityName = ?, @Payload = ?",
                entityName,
                toJson(columns)
        );
    }

    private static String toJson(Map<String, ?> columns) {
        StringBuilder json = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, ?> column : columns.entrySet()) {
            if (!first) {
                json.append(',');
            }
            first = false;
            json.append(quote(column.getKey())).append(':').append(value(column.getValue()));
        }
        return json.append('}').toString();
    }

    private static String value(Object value) {
        if (value == null) {
            return "null";
        }
        if (value instanceof BigDecimal decimal) {
            return decimal.toPlainString();
        }
        if (value instanceof Number || value instanceof Boolean) {
            return value.toString();
        }
        if (value instanceof LocalDate date) {
            return quote(date.toString());
        }
        if (value instanceof LocalTime time) {
            return quote(time.format(TIME_FORMAT));
        }
        if (value instanceof LocalDateTime dateTime) {
            return quote(dateTime.format(DATE_TIME_FORMAT));
        }
        return quote(value.toString());
    }

    private static String quote(String value) {
        StringBuilder escaped = new StringBuilder("\"");
        for (int index = 0; index < value.length(); index++) {
            char character = value.charAt(index);
            switch (character) {
                case '"' -> escaped.append("\\\"");
                case '\\' -> escaped.append("\\\\");
                case '\b' -> escaped.append("\\b");
                case '\f' -> escaped.append("\\f");
                case '\n' -> escaped.append("\\n");
                case '\r' -> escaped.append("\\r");
                case '\t' -> escaped.append("\\t");
                default -> {
                    if (character < 0x20) {
                        escaped.append(String.format("\\u%04x", (int) character));
                    } else {
                        escaped.append(character);
                    }
                }
            }
        }
        return escaped.append('"').toString();
    }
}