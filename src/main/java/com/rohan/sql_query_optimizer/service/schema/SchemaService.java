package com.rohan.sql_query_optimizer.service.schema;

import com.rohan.sql_query_optimizer.service.db.DBConnectionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Service
public class SchemaService {
    private final DBConnectionManager dbConnectionManager;

    @Autowired
    public SchemaService(DBConnectionManager dbConnectionManager) {
        this.dbConnectionManager = dbConnectionManager;
    }

    public String getSchema() throws SQLException {
        Map<String, List<String>> schemaMap = initSchemaMap();
        return stringifySchemaMap(schemaMap);
    }

    private Map<String, List<String>> initSchemaMap() throws SQLException {
        Map<String, List<String>> schemaMap = new LinkedHashMap<>();
        Connection connection = dbConnectionManager.getConnection();

        try (PreparedStatement stmt = connection.prepareStatement("SELECT table_name, column_name FROM information_schema.columns WHERE table_schema = 'public'");
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String table = rs.getString("table_name");
                String column = rs.getString("column_name");

                schemaMap
                        .computeIfAbsent(table, t -> new ArrayList<>())
                        .add(column);
            }
        }

        return schemaMap;
    }

    private String stringifySchemaMap(Map<String, List<String>> schemaMap) {
        StringBuilder sb = new StringBuilder();

        sb.append("Tables:\n\n");

        for (Map.Entry<String, List<String>> entry : schemaMap.entrySet()) {
            String tableName = entry.getKey();
            List<String> columns = entry.getValue();

            sb.append(tableName)
                    .append("(")
                    .append(String.join(", ", columns))
                    .append(")\n");
        }

        return sb.toString();
    }
}
