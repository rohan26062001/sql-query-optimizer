package com.rohan.sql_query_optimizer.service.query;

import com.rohan.sql_query_optimizer.dto.ai.AiGeneratedQuery;
import com.rohan.sql_query_optimizer.dto.user.UserOutput;
import com.rohan.sql_query_optimizer.service.db.DBConnectionManager;
import com.rohan.sql_query_optimizer.utils.ResultSetUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * The type Query executor service.
 */
@Service
public class QueryExecutorService {

    @Autowired
    private DBConnectionManager dbConnectionManager;

    /**
     * Execute user output.
     *
     * @param aiGeneratedQuery the ai generated query
     * @return the user output
     * @throws SQLException the sql exception
     */
    public UserOutput execute(AiGeneratedQuery aiGeneratedQuery) throws SQLException {
        try (Connection connection = dbConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(aiGeneratedQuery.getQuery());
             ResultSet resultSet = preparedStatement.executeQuery()) {
            ResultSetUtil.printResultSet(resultSet);
            return new UserOutput();
        }
    }
}
