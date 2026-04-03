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

    private ResultSetUtil() {
        // Private Constructor
    }

    /**
     * To user output string.
     *
     * @param rs the rs
     * @return the string
     * @throws SQLException the sql exception
     */
    public static String  toUserOutput(ResultSet rs) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();

        int[] columnWidths = new int[columnCount];
        String[] columnNames = new String[columnCount];
        for (int i = 1; i <= columnCount; i++) {
            columnNames[i - 1] = metaData.getColumnName(i);
            columnWidths[i - 1] = columnNames[i - 1].length();
        }

        List<String[]> rows = new ArrayList<>();
        while (rs.next()) {
            String[] row = new String[columnCount];
            for (int i = 1; i <= columnCount; i++) {
                Object value = rs.getObject(i);
                row[i - 1] = (value == null) ? "NULL" : value.toString();
                columnWidths[i - 1] = Math.max(columnWidths[i - 1], row[i - 1].length());
            }
            rows.add(row);
        }

        // ✅ Build into StringBuilder instead of printing
        StringBuilder sb = new StringBuilder();

        appendSeparator(sb, columnWidths);

        sb.append("| ");
        for (int i = 0; i < columnCount; i++) {
            sb.append(padRight(columnNames[i], columnWidths[i])).append(" | ");
        }
        sb.append("\n");

        appendSeparator(sb, columnWidths);

        for (String[] row : rows) {
            sb.append("| ");
            for (int i = 0; i < columnCount; i++) {
                sb.append(padRight(row[i], columnWidths[i])).append(" | ");
            }
            sb.append("\n");
        }

        appendSeparator(sb, columnWidths);

        return sb.toString();
    }

    private static void appendSeparator(StringBuilder sb, int[] columnWidths) {
        sb.append("+");
        for (int width : columnWidths) {
            sb.append("-".repeat(width + 2)).append("+");
        }
        sb.append("\n");
    }

    private static String padRight(String text, int width) {
        return String.format("%-" + width + "s", text);
    }
}
