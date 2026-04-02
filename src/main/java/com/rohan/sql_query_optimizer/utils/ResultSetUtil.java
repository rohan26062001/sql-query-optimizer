package com.rohan.sql_query_optimizer.utils;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * The type Result set util.
 */
public class ResultSetUtil {

    /**
     * Print result set.
     *
     * @param rs the rs
     * @throws SQLException the sql exception
     */
    public static void printResultSet(ResultSet rs) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();

        // Store column widths
        int[] columnWidths = new int[columnCount];

        // Get column names and initialize widths
        String[] columnNames = new String[columnCount];
        for (int i = 1; i <= columnCount; i++) {
            columnNames[i - 1] = metaData.getColumnName(i);
            columnWidths[i - 1] = columnNames[i - 1].length();
        }

        // Store all rows temporarily
        List<String[]> rows = new ArrayList<>();

        while (rs.next()) {
            String[] row = new String[columnCount];
            for (int i = 1; i <= columnCount; i++) {
                Object value = rs.getObject(i);
                row[i - 1] = (value == null) ? "NULL" : value.toString();

                // Update max width
                columnWidths[i - 1] = Math.max(columnWidths[i - 1], row[i - 1].length());
            }
            rows.add(row);
        }

        // Print separator
        printSeparator(columnWidths);

        // Print header
        System.out.print("| ");
        for (int i = 0; i < columnCount; i++) {
            System.out.print(padRight(columnNames[i], columnWidths[i]) + " | ");
        }
        System.out.println();

        // Print separator
        printSeparator(columnWidths);

        // Print rows
        for (String[] row : rows) {
            System.out.print("| ");
            for (int i = 0; i < columnCount; i++) {
                System.out.print(padRight(row[i], columnWidths[i]) + " | ");
            }
            System.out.println();
        }

        // Print final separator
        printSeparator(columnWidths);
    }

    // Helper to print separator line
    private static void printSeparator(int[] columnWidths) {
        System.out.print("+");
        for (int width : columnWidths) {
            System.out.print("-".repeat(width + 2) + "+");
        }
        System.out.println();
    }

    // Helper to pad text
    private static String padRight(String text, int width) {
        return String.format("%-" + width + "s", text);
    }
}
