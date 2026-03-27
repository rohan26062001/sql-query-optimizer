package com.rohan.sql_query_optimizer.service.query;

import com.rohan.sql_query_optimizer.dto.ai.AiGeneratedQuery;
import com.rohan.sql_query_optimizer.dto.user.UserOutput;
import com.rohan.sql_query_optimizer.service.db.DBConnectionManager;
import com.rohan.sql_query_optimizer.utils.ResultSetUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class QueryExecutorService {

    @Autowired
    private DBConnectionManager dbConnectionManager;

    public UserOutput execute(AiGeneratedQuery aiGeneratedQuery) throws SQLException {
        Connection connection = dbConnectionManager.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(aiGeneratedQuery.getQuery());

        ResultSet resultSet = preparedStatement.executeQuery();

        ResultSetUtil.printResultSet(resultSet);

        preparedStatement.close();
        return new UserOutput();
    }
}
