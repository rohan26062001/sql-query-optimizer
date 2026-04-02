package com.rohan.sql_query_optimizer.service;

import com.rohan.sql_query_optimizer.dto.ai.AiGeneratedQuery;
import com.rohan.sql_query_optimizer.dto.user.UserInput;
import com.rohan.sql_query_optimizer.dto.user.UserOutput;
import com.rohan.sql_query_optimizer.service.calcite.QueryValidatorService;
import com.rohan.sql_query_optimizer.service.query.QueryExecutorService;
import com.rohan.sql_query_optimizer.service.query.QueryGenAiService;
import lombok.CustomLog;
import org.springframework.stereotype.Service;

import java.sql.SQLException;

/**
 * The type Base service.
 */
@Service
@CustomLog
public class BaseService {
    private QueryGenAiService queryGenAiService;
    private QueryValidatorService queryValidatorService;
    private QueryExecutorService queryExecutorService;

    /**
     * Instantiates a new Base service.
     *
     * @param queryGenAiService     the query gen ai service
     * @param queryValidatorService the query validator service
     * @param queryExecutorService  the query executor service
     */
    public BaseService(QueryGenAiService queryGenAiService,  QueryValidatorService queryValidatorService,
                       QueryExecutorService queryExecutorService) {
        this.queryGenAiService = queryGenAiService;
        this.queryValidatorService = queryValidatorService;
        this.queryExecutorService = queryExecutorService;
    }

    /**
     * Execute user output.
     *
     * @param userInput the user input
     * @return the user output
     * @throws SQLException the sql exception
     */
    public UserOutput execute(UserInput userInput) throws SQLException {
        log.debug("User Input: {}", userInput);
        AiGeneratedQuery aiGeneratedQuery = queryGenAiService.generateQuery(userInput);
        log.debug("Generated query: {}", aiGeneratedQuery.getQuery());
        AiGeneratedQuery validatedQuery = queryValidatorService.validate(userInput, aiGeneratedQuery);
        log.debug("Validated query: {}", validatedQuery.getQuery());
        return queryExecutorService.execute(validatedQuery);
    }
}
