package com.github.mseeger.sql;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

/**
 * This class is using reflection in order to extract field names and values
 * from an entity object, returning the values converted to strings.
 *
 * A class is an entity if all its fields have public getters, and there are
 * no other methods of name "getXyz". The field names are determined from the
 * getter names.
 */
class EntityToRecordConverter {
    private final Class<?> entityType;
    private final EntityField[] fields;

    /**
     * If `this.fields` is determined by reflection, the ordering of entries
     * is not defined. But if `entityType` has a static `String[]` field named
     * `columnNames`, these column names are used in this ordering.
     *
     * @param entityType Entity class
     * @param formats Maps certain field names to format strings. For all other
     *                fields, the default conversion is used
     */
    public EntityToRecordConverter(
            Class<?> entityType,
            Map<String, String> formats
    ) {
        this.entityType = entityType;
        this.fields = createFields(entityType, formats);
    }

    public EntityToRecordConverter(Class<?> entityType) {
        this(entityType, new HashMap<String, String>());
    }

    private EntityField[] createFields(Class<?> entityType, Map<String, String> formats) {
        ArrayList<EntityField> result = new ArrayList<>();
        for (var method: entityType.getDeclaredMethods()) {
            var methodName = method.getName();
            if (methodName.startsWith("get")) {
                var format = formats.get(methodName);
                result.add(new EntityField(method, format));
            }
        }
        return postProcessFields(entityType, result);
    }

    /**
     * Checks for static `columnNames` field in `entityType`. If given, this
     * determines the column names and their ordering.
     */
    private EntityField[] postProcessFields(
            Class<?> entityType,
            ArrayList<EntityField> fields
    ) {
        String[] columnNames = columnNamesByReflection(entityType);
        if (columnNames != null) {
            ArrayList<EntityField> newFields = new ArrayList<>();
            for (var name : columnNames) {
                var field = fields.stream()
                        .filter(elem -> elem.getName().equals(name))
                        .findFirst();
                newFields.add(
                        field.orElseThrow(
                                () -> new NoSuchElementException("columnNames entry '" + name + "' is no entity field")
                        )
                );
            }
            fields = newFields;
        }
        return fields.toArray(new EntityField[0]);
    }

    private String[] columnNamesByReflection(Class<?> entityType) {
        String[] columnNames = null;
        try {
            Field field = entityType.getField("columnNames");
            if (
                    Modifier.isStatic(field.getModifiers()) &&
                    field.getType() == String[].class
            ) {
                columnNames = (String[]) field.get(null);
            }
        } catch (NoSuchFieldException | IllegalAccessException _) {}
        return columnNames;
    }

    public String[] getFieldNames() {
        // See https://stackoverflow.com/questions/37192045/java-creating-an-array-from-the-properties-of-another-array
        return Stream.of(fields).map(EntityField::getName).toArray(String[]::new);
    }

    /**
     * @param entity Entity object
     * @return Ordered dictionary mapping field name to value converted to string
     */
    public String[] fieldValuesAsStrings(Object entity) {
        if (entity.getClass() != entityType)
            throw new IllegalArgumentException(
                    "entity has wrong type, must be '" + entityType.getName() + "'"
            );
        return Stream.of(fields).map(field -> field.getValueAsString(entity)).toArray(String[]::new);
    }
}