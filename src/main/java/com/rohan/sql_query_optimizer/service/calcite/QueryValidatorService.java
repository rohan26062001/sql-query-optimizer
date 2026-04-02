package com.rohan.sql_query_optimizer.service.calcite;

import com.rohan.sql_query_optimizer.dto.ai.AiGeneratedQuery;
import com.rohan.sql_query_optimizer.dto.user.UserInput;
import com.rohan.sql_query_optimizer.service.db.DBConnectionManager;
import com.rohan.sql_query_optimizer.service.query.QueryGenAiService;
import lombok.CustomLog;
import org.springframework.stereotype.Service;

import java.sql.SQLException;

/**
 * The type Query validator service.
 */
@Service
@CustomLog
public class QueryValidatorService {
    private DBConnectionManager dbConnectionManager;
    private QueryGenAiService queryGenAiService;

    /**
     * Instantiates a new Query validator service.
     *
     * @param dbConnectionManager the db connection manager
     * @param queryGenAiService   the query gen ai service
     */
    public QueryValidatorService(DBConnectionManager dbConnectionManager, QueryGenAiService queryGenAiService) {
        this.dbConnectionManager = dbConnectionManager;
        this.queryGenAiService = queryGenAiService;
    }

    /**
     * Validate ai generated query.
     *
     * @param userInput        the user input
     * @param aiGeneratedQuery the ai generated query
     * @return the ai generated query
     * @throws SQLException the sql exception
     */
    public AiGeneratedQuery validate(UserInput userInput, AiGeneratedQuery aiGeneratedQuery) throws SQLException {
        DynamicSchemaValidator dynamicSchemaValidator = dbConnectionManager.getValidator();
        int attempts = 3;
        AiGeneratedQuery query = aiGeneratedQuery;
        while(attempts > 0) {
            String validationResult = dynamicSchemaValidator.validate(query.getQuery());
            if(validationResult == null) {
                log.debug("Query '{}' is validated successfully.", query.getQuery());
                return query;
            }

            attempts--;
            log.debug("Error occurred while validating Query '{}'. \nReason: {} \nRemaining Attempts: {}", query, validationResult, attempts);
            query = queryGenAiService.rectifyQuery(userInput, query, validationResult);
        }

        throw new RuntimeException("Cannot generate correct query after 3 attempts");
    }
}
