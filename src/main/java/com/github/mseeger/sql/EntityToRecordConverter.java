package com.github.mseeger.sql;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
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
     * @param entityType Entity class aaa
     * @param formats Maps certain field names to format strings. For all other
     *                fields, the default conversion is used
     */
    public EntityToRecordConverter(Class<?> entityType, Map<String, String> formats) {
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
        return result.toArray(new EntityField[0]);
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