package com.rohan.sql_query_optimizer.service.db;

import com.rohan.sql_query_optimizer.dto.db.DatabaseConnectionRequest;
import com.rohan.sql_query_optimizer.dto.db.DatabaseConnectionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;

/**
 * The type Db service.
 */
@Service
public class DBService {

    @Autowired
    private DBConnectionManager dbConnectionManager;

    /**
     * Connect database connection response.
     *
     * @param params the params
     * @return the database connection response
     * @throws SQLException the sql exception
     */
    public DatabaseConnectionResponse connect(DatabaseConnectionRequest params) throws SQLException {
        return dbConnectionManager.connect(params);
    }

    /**
     * Close string.
     *
     * @return the string
     * @throws SQLException the sql exception
     */
    public String close() throws SQLException {
        return dbConnectionManager.close();
    }
}
