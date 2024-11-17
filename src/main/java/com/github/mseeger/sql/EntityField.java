package com.github.mseeger.sql;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;

public class EntityField {
    private static final String defaultFloatFormat = "%.2f";

    private final String name;
    private final Method getter;
    private final EntryToStringConverter converter;

    public EntityField(Method getter, String format) {
        this.getter = getter;
        var methodName = getter.getName();
        if (!methodName.startsWith("get"))
            throw new IllegalArgumentException("Method " + methodName + " must start with 'get'");
        // Infer field name from name of getter method
        this.name = methodName.substring(3, 4).toLowerCase() + methodName.substring(4);
        this.converter = createConverter(getter, format);
    }

    public EntityField(Method getter) {
        this(getter, null);
    }

    private EntryToStringConverter createConverter(Method getter, String format) {
        if (format == null) {
            var fieldType = getter.getReturnType();
            System.out.printf("%s: %s\n", getter.getName(), fieldType);
            if (fieldType == float.class
                    || fieldType == double.class
                    || fieldType == short.class
                    || fieldType == Float.class
                    || fieldType == Double.class
                    || fieldType == Short.class
                    || fieldType == BigDecimal.class
            ) {
                System.out.println("FLOAT!");
                format = defaultFloatFormat;
            }
        }
        if (format == null)
            return new DefaultEntryToStringConverter();
        else
            return new FormatEntryToStringConverter(format);
    }

    public String getName() {
        return name;
    }

    public Object getValue(Object entity) {
        try {
            return getter.invoke(entity);
        } catch (InvocationTargetException | IllegalAccessException e) {
            var ex = new IllegalArgumentException("Cannot get value of '" + name + "' from entity");
            ex.initCause(e);
            throw ex;
        }
    }

    public String getValueAsString(Object entity) {
        return converter.convert(getValue(entity));
    }
}
