package com.rohan.sql_query_optimizer.service.db;

import com.rohan.sql_query_optimizer.dto.db.DatabaseConnectionParams;
import com.rohan.sql_query_optimizer.dto.db.DatabaseConnectionResponse;
import com.rohan.sql_query_optimizer.enums.db.DBConnectionStatus;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Service
public class DBConnectionManager {
    private Connection connection;

    public DatabaseConnectionResponse connect(DatabaseConnectionParams params) throws SQLException {
        // Check if the connection is already closed
        if (this.connection != null && !this.connection.isClosed()) {
            this.connection.close();
        }

        String url = String.format("jdbc:postgresql://%s:%s/%s", params.getHost(), params.getPort(), params.getDatabase());
        this.connection = DriverManager.getConnection(url, params.getUsername(), params.getPassword());
        return new DatabaseConnectionResponse(DBConnectionStatus.SUCCESS, String.format("Connected to Database %s", url));
    }

    public Connection getConnection() {
        if (connection == null)
            throw new RuntimeException("No DB connection established");
        return connection;
    }
}
