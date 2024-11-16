package com.github.mseeger.sql;

class DefaultEntryToStringConverter implements EntryToStringConverter {
    @Override
    public String convert(Object x) {
        return x.toString();
    }
}
