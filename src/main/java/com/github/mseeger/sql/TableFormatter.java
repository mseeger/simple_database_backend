package com.github.mseeger.sql;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

public class TableFormatter {
    public static String asString(
            ArrayList<?> queryResult,
            Map<String, String> formats
    ) {
        if (queryResult.isEmpty()) return "";
        Class<?> entityType = queryResult.getFirst().getClass();
        var converter = new EntityToRecordConverter(entityType, formats);
        var fieldNames = converter.getFieldNames();
        String[][] fieldValues = Stream.of(queryResult.toArray())
                .map(converter::fieldValuesAsStrings)
                .toArray(String[][]::new);
        int[] columnWidths = getColumnWidths(fieldNames, fieldValues);
        // HIER!!
    }

    public static String asString(ArrayList<?> queryResult) {
        return asString(queryResult, new HashMap<String, String>());
    }

    private static int[] getColumnWidths(
            String[] fieldNames,
            String[][] fieldValues
    ) {
        Function<String[], int[]> lengthMap = (String[] values) ->
                Stream.of(values).mapToInt(String::length).toArray();
        var maxLengths = lengthMap.apply(fieldNames);
        for (var values : fieldValues) {
            var lengths = lengthMap.apply(values);
            for (int i = 0; i < maxLengths.length; i++)
                maxLengths[i] = Math.max(maxLengths[i], lengths[i]);
        }
        return maxLengths;
    }
}
