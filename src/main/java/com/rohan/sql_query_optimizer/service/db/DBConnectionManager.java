package com.rohan.sql_query_optimizer.service.db;

import com.rohan.sql_query_optimizer.dto.db.DatabaseConnectionRequest;
import com.rohan.sql_query_optimizer.dto.db.DatabaseConnectionResponse;
import com.rohan.sql_query_optimizer.enums.db.DBConnectionStatus;
import com.rohan.sql_query_optimizer.service.calcite.DynamicSchemaValidator;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * The type Db connection manager.
 */
@Service
public class DBConnectionManager {
    private DataSource dataSource;
    private DynamicSchemaValidator validator;

    /**
     * Connect database connection response.
     *
     * @param params the params
     * @return the database connection response
     * @throws SQLException the sql exception
     */
    public synchronized DatabaseConnectionResponse connect(DatabaseConnectionRequest params) throws SQLException {
        // Check if the connection is already closed
        if (this.dataSource != null && dataSource instanceof HikariDataSource hds && !hds.isClosed()) {
            hds.close();
        }

        HikariConfig hikariConfig = new HikariConfig();

        String url = String.format("jdbc:postgresql://%s:%s/%s", params.getHost(), params.getPort(), params.getDatabase());
        hikariConfig.setJdbcUrl(url);
        hikariConfig.setUsername(params.getUsername());
        hikariConfig.setPassword(params.getPassword());

        this.dataSource = new HikariDataSource(hikariConfig);

        // ✅ create validator once after new connection, not on every request
        try (Connection conn = this.dataSource.getConnection()) {
            this.validator = new DynamicSchemaValidator(this.dataSource, conn.getSchema());
        }

        return new DatabaseConnectionResponse(DBConnectionStatus.SUCCESS, String.format("Connected to Database %s", url));
    }

    /**
     * Gets data source.
     *
     * @return the data source
     */
    public DataSource getDataSource() {
        if (dataSource == null)
            throw new RuntimeException("No DataSource established");
        return dataSource;
    }

    /**
     * Gets connection.
     *
     * @return the connection
     * @throws SQLException the sql exception
     */
    public Connection getConnection() throws SQLException {
        return getDataSource().getConnection();
    }

    /**
     * Gets validator.
     *
     * @return the validator
     */
    public DynamicSchemaValidator getValidator() {
        if (validator == null)
            throw new RuntimeException("No DB connection established");
        return validator;
    }


    /**
     * Close string.
     *
     * @return the string
     */
    public synchronized String close() {
        try {
            if (dataSource instanceof HikariDataSource hds && !hds.isClosed()) {
                hds.close();
                dataSource = null;
            }
            return "Connection closure successful";
        } catch (Exception e) {
            return e.getMessage();
        }
    }
}
