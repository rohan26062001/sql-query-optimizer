package com.rohan.sql_query_optimizer.service;

import com.rohan.sql_query_optimizer.dto.ai.AiGeneratedQuery;
import com.rohan.sql_query_optimizer.dto.user.UserInput;
import com.rohan.sql_query_optimizer.dto.user.UserOutput;
import com.rohan.sql_query_optimizer.service.query.QueryGenAiService;
import com.rohan.sql_query_optimizer.service.query.QueryExecutorService;
import org.springframework.stereotype.Service;

import java.sql.SQLException;

@Service
public class BaseService {

    private QueryGenAiService queryGenAiService;
    private QueryExecutorService queryExecutorService;

    public BaseService(QueryGenAiService queryGenAiService, QueryExecutorService queryExecutorService) {
        this.queryGenAiService = queryGenAiService;
        this.queryExecutorService = queryExecutorService;
    }

    public UserOutput execute(UserInput userInput) throws SQLException {
        AiGeneratedQuery aiGeneratedQuery = queryGenAiService.generateQuery(userInput);
        UserOutput userOutput = queryExecutorService.execute(aiGeneratedQuery);
        return userOutput;
    }
}
