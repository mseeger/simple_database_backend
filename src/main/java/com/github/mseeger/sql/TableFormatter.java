package com.github.mseeger.sql;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class TableFormatter {
    public static final int spaceBetweenColumns = 2;

    /**
     * Returns which contains a formatted table corresponding to the query
     * result `queryResult`. Uses reflection on the getters of the entity
     * class in order to determine the column names and types.
     * <br>
     * If the entity class has a static `String[]` field named
     * `columnNames`, the column names are taken from there and
     * compared to the names obtained by reflection (the former must
     * be a subset). Providing `columnNames` determines the column
     * ordering. If column names are obtained by reflection, the
     * ordering of column names is not defined.
     *
     * @param queryResult List of entities as returned by a query
     * @param formats Optional. Maps column names to format strings.
     *                Defaults are used for all other columns
     * @return Formatted table as string
     */
    public static String asString(
            ArrayList<?> queryResult,
            Map<String, String> formats
    ) {
        if (queryResult.isEmpty()) return "";
        Class<?> entityType = queryResult.getFirst().getClass();
        var converter = new EntityToRecordConverter(entityType, formats);
        String[] fieldNames = converter.getFieldNames();
        String[][] fieldValues = Stream.of(queryResult.toArray())
                .map(converter::fieldValuesAsStrings)
                .toArray(String[][]::new);
        int[] columnWidths = getColumnWidths(fieldNames, fieldValues);
        String formatString = getFormatString(columnWidths);
        int rowWidth = IntStream.of(columnWidths).sum()
                + spaceBetweenColumns * (columnWidths.length - 1);
        String separator = "-".repeat(rowWidth);
        Function<String[], String> formatRow = values ->
                String.format(formatString, (Object[]) values);
        String header = formatRow.apply(fieldNames)
                + "\n"
                + separator
                + "\n";
        String body = String.join(
                "\n",
                Stream.of(fieldValues).map(formatRow).toArray(String[]::new)
        );
        return header + body;
    }

    public static String asString(ArrayList<?> queryResult) {
        return asString(queryResult, new HashMap<>());
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

    private static String getFormatString(int[] columnWidths) {
        String[] formats = IntStream.of(columnWidths)
                .mapToObj(width -> "%" + width + "s")
                .toArray(String[]::new);
        return String.join(" ".repeat(spaceBetweenColumns), formats);
    }

    public static void main(String[] args) {
        class MyEntity {
            public static final String[] columnNames = {
                    "userID",
                    "lastName",
                    "birthDate",
                    "timeStamp",
                    "salary",
                    "x"
            };

            private final int userID;
            private final String lastName;
            private final LocalDate birthDate;
            private final LocalDateTime timeStamp;
            private final float salary;
            private final double x;

            public MyEntity(int userID, String lastName, LocalDate birthDate, LocalDateTime timeStamp, float salary, double x) {
                this.userID = userID;
                this.lastName = lastName;
                this.birthDate = birthDate;
                this.timeStamp = timeStamp;
                this.salary = salary;
                this.x = x;
            }

            public int getUserID() {
                return userID;
            }

            public String getLastName() {
                return lastName;
            }

            public LocalDate getBirthDate() {
                return birthDate;
            }

            public LocalDateTime getTimeStamp() {
                return timeStamp;
            }

            public float getSalary() {
                return salary;
            }

            public double getX() {
                return x;
            }
        }
        MyEntity[] resultArray = {
                new MyEntity(12, "Jones", LocalDate.parse("1970-06-28"), LocalDateTime.parse("2024-01-01T10:15:30"), 5124.46F, 123.456),
                new MyEntity(13, "May", LocalDate.parse("1990-12-31"), LocalDateTime.parse("2024-02-02T11:49:59"), 6254.38F, 123456.789),
                new MyEntity(14, "Beethoven", LocalDate.parse("1976-01-04"), LocalDateTime.parse("2022-12-15T10:00:00"), 7124F, 123456789.012),
                new MyEntity(122, "Carmichael", LocalDate.parse("1999-12-31"), LocalDateTime.parse("2023-12-31T23:59:59"), 1234.56F, 0.012),
                new MyEntity(1234, "Kirkpatrick", LocalDate.parse("1945-06-13"), LocalDateTime.parse("2024-01-01T00:00:00"), 9999.99F, 11.119),
                new MyEntity(1, "Xi", LocalDate.parse("1956-07-14"), LocalDateTime.parse("2024-01-01T10:15:30"), 12345.67F, 1.234),
        };
        ArrayList<MyEntity> queryResult = new ArrayList<>(Arrays.asList(resultArray));
        var formattedTable = TableFormatter.asString(queryResult);
        System.out.println(formattedTable);
    }
}
