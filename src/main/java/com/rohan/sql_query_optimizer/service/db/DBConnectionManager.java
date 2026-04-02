package com.rohan.sql_query_optimizer.service.db;

import com.rohan.sql_query_optimizer.dto.db.DatabaseConnectionRequest;
import com.rohan.sql_query_optimizer.dto.db.DatabaseConnectionResponse;
import com.rohan.sql_query_optimizer.enums.db.DBConnectionStatus;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Service
public class DBConnectionManager {
    private Connection connection;
    private DataSource dataSource;

    public synchronized DatabaseConnectionResponse connect(DatabaseConnectionRequest params) throws SQLException {
        // Check if the connection is already closed
        if (this.connection != null && !this.connection.isClosed()) {
            this.connection.close();
        }

        String url = String.format("jdbc:postgresql://%s:%s/%s", params.getHost(), params.getPort(), params.getDatabase());
        this.connection = DriverManager.getConnection(url, params.getUsername(), params.getPassword());

        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(url);
        hikariConfig.setUsername(params.getUsername());
        hikariConfig.setPassword(params.getPassword());
        this.dataSource = new HikariDataSource(hikariConfig);

        return new DatabaseConnectionResponse(DBConnectionStatus.SUCCESS, String.format("Connected to Database %s", url));
    }

    public Connection getConnection() {
        if (connection == null)
            throw new RuntimeException("No DB connection established");
        return connection;
    }

    public DataSource getDataSource() {
        if (dataSource == null)
            throw new RuntimeException("No DataSource established");
        return dataSource;
    }


    public String close() throws SQLException {
        String message = "Connection closure successful";
        try {
            if(connection != null) {
                connection.close();
                connection = null;
            }
            if(dataSource != null)
                dataSource = null;
        } catch (Exception e) {
            message = e.getMessage();
        } finally {
            return message;
        }
    }
}
