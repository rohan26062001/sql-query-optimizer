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

@Service
public class QueryExecutorService {

    @Autowired
    private DBConnectionManager dbConnectionManager;

    public UserOutput execute(AiGeneratedQuery aiGeneratedQuery) throws SQLException {
        try (Connection connection = dbConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(aiGeneratedQuery.getQuery());
             ResultSet resultSet = preparedStatement.executeQuery()) {
            ResultSetUtil.printResultSet(resultSet);
            return new UserOutput();
        }
    }
}
