package com.rohan.sql_query_optimizer.service;

import com.rohan.sql_query_optimizer.dto.ai.AiGeneratedQuery;
import com.rohan.sql_query_optimizer.dto.user.UserInput;
import com.rohan.sql_query_optimizer.dto.user.UserOutput;
import com.rohan.sql_query_optimizer.service.calcite.QueryValidatorService;
import com.rohan.sql_query_optimizer.service.query.QueryGenAiService;
import com.rohan.sql_query_optimizer.service.query.QueryExecutorService;
import org.springframework.stereotype.Service;

import java.sql.SQLException;

@Service
public class BaseService {

    private QueryGenAiService queryGenAiService;
    private QueryValidatorService queryValidatorService;
    private QueryExecutorService queryExecutorService;

    public BaseService(QueryGenAiService queryGenAiService,  QueryValidatorService queryValidatorService,
                       QueryExecutorService queryExecutorService) {
        this.queryGenAiService = queryGenAiService;
        this.queryValidatorService = queryValidatorService;
        this.queryExecutorService = queryExecutorService;
    }

    public UserOutput execute(UserInput userInput) throws SQLException {
        AiGeneratedQuery aiGeneratedQuery = queryGenAiService.generateQuery(userInput);
        AiGeneratedQuery validatedQuery = queryValidatorService.validate(userInput, aiGeneratedQuery);
        return queryExecutorService.execute(validatedQuery);
    }
}
