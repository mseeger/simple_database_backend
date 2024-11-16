package com.github.mseeger.sql;

class FormatEntryToStringConverter implements EntryToStringConverter {
    public static final String defaultFormat = ".2f";

    private final String format;

    public FormatEntryToStringConverter(String format) {
        this.format = format;
    }

    public FormatEntryToStringConverter() {
        this(defaultFormat);
    }

    @Override
    public String convert(Object x) {
        return String.format(format, x);
    }
}
