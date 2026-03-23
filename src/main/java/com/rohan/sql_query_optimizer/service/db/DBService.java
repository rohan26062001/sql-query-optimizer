package com.rohan.sql_query_optimizer.service.db;

import com.rohan.sql_query_optimizer.dto.db.DatabaseConnectionParams;
import com.rohan.sql_query_optimizer.dto.db.DatabaseConnectionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;

@Service
public class DBService {

    @Autowired
    private DBConnectionManager dbConnectionManager;

    public DatabaseConnectionResponse connect(DatabaseConnectionParams params) throws SQLException {
        return dbConnectionManager.connect(params);
    }
}
