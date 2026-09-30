package com.nhom12.hospital.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.StoredProcedureQuery;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class StoredProcedureJpaRepository<T, ID> extends SimpleJpaRepository<T, ID> {

    private final JpaEntityInformation<T, ?> entityInformation;
    private final EntityManager entityManager;
    private final String entityName;

    public StoredProcedureJpaRepository(JpaEntityInformation<T, ?> entityInformation,
                                        EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.entityInformation = entityInformation;
        this.entityManager = entityManager;
        this.entityName = resolveEntityName(entityInformation.getJavaType());
    }

    @Override
    @Transactional
    public <S extends T> S save(S entity) {
        StoredProcedureQuery procedure = entityManager.createStoredProcedureQuery("dbo.usp_Entity_Save");
        procedure.registerStoredProcedureParameter("EntityName", String.class, ParameterMode.IN);
        procedure.registerStoredProcedureParameter("Payload", String.class, ParameterMode.IN);
        procedure.setParameter("EntityName", entityName);
        procedure.setParameter("Payload", serializeColumns(entity));
        procedure.execute();
        return entity;
    }

    @Override
    @Transactional
    public <S extends T> List<S> saveAll(Iterable<S> entities) {
        List<S> saved = new ArrayList<>();
        for (S entity : entities) {
            saved.add(save(entity));
        }
        return saved;
    }

    @Override
    @Transactional
    public <S extends T> S saveAndFlush(S entity) {
        return save(entity);
    }

    @Override
    @Transactional
    public <S extends T> List<S> saveAllAndFlush(Iterable<S> entities) {
        return saveAll(entities);
    }

    @Override
    @Transactional
    public void deleteById(ID id) {
        if (id == null) {
            throw new IllegalArgumentException("The given id must not be null");
        }
        StoredProcedureQuery procedure = entityManager.createStoredProcedureQuery("dbo.usp_Entity_Delete");
        procedure.registerStoredProcedureParameter("EntityName", String.class, ParameterMode.IN);
        procedure.registerStoredProcedureParameter("IdValue", String.class, ParameterMode.IN);
        procedure.setParameter("EntityName", entityName);
        procedure.setParameter("IdValue", id.toString());
        procedure.execute();
    }

    @Override
    @Transactional
    public void delete(T entity) {
        if (entity == null) {
            throw new IllegalArgumentException("The entity must not be null");
        }
        Object id = entityInformation.getId(entity);
        if (id == null) {
            throw new IllegalArgumentException("The entity id must not be null");
        }
        deleteById((ID) id);
    }

    @Override
    @Transactional
    public void deleteAllById(Iterable<? extends ID> ids) {
        for (ID id : ids) {
            deleteById(id);
        }
    }

    @Override
    @Transactional
    public void deleteAllByIdInBatch(Iterable<ID> ids) {
        for (ID id : ids) {
            deleteById(id);
        }
    }

    @Override
    @Transactional
    public void deleteAll(Iterable<? extends T> entities) {
        for (T entity : entities) {
            delete(entity);
        }
    }

    @Override
    @Transactional
    public void deleteAllInBatch(Iterable<T> entities) {
        deleteAll(entities);
    }

    @Override
    @Transactional
    public void deleteAll() {
        deleteAll(findAll());
    }

    @Override
    @Transactional
    public void deleteAllInBatch() {
        deleteAll(findAll());
    }

    private String serializeColumns(Object entity) {
        Map<String, Object> values = new LinkedHashMap<>();
        for (Class<?> type = entity.getClass(); type != null && type != Object.class; type = type.getSuperclass()) {
            for (Field field : type.getDeclaredFields()) {
                Column column = field.getAnnotation(Column.class);
                if (column == null || (!column.insertable() && !column.updatable())) {
                    continue;
                }
                try {
                    field.setAccessible(true);
                    String columnName = column.name().isBlank() ? field.getName() : column.name();
                    values.put(columnName, field.get(entity));
                } catch (IllegalAccessException error) {
                    throw new IllegalStateException("Cannot read entity column " + field.getName(), error);
                }
            }
        }
        StringBuilder json = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, Object> entry : values.entrySet()) {
            if (!first) {
                json.append(',');
            }
            first = false;
            json.append(quoteJson(entry.getKey())).append(':').append(jsonValue(entry.getValue()));
        }
        return json.append('}').toString();
    }

    private String jsonValue(Object value) {
        if (value == null) {
            return "null";
        }
        if (value instanceof Number || value instanceof Boolean) {
            return value.toString();
        }
        if (value instanceof BigDecimal decimal) {
            return decimal.toPlainString();
        }
        if (value instanceof LocalDateTime dateTime) {
            return quoteJson(dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")));
        }
        if (value instanceof LocalDate date) {
            return quoteJson(date.toString());
        }
        if (value instanceof LocalTime time) {
            return quoteJson(time.format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS")));
        }
        if (value instanceof TemporalAccessor) {
            return quoteJson(value.toString());
        }
        return quoteJson(value.toString());
    }

    private String quoteJson(String value) {
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

    private String resolveEntityName(Class<?> entityType) {
        Table table = entityType.getAnnotation(Table.class);
        String tableName = table == null || table.name().isBlank() ? entityType.getSimpleName() : table.name();
        return tableName.startsWith("vw_") ? tableName.substring(3) : tableName;
    }

}
