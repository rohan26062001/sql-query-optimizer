package com.rohan.sql_query_optimizer.service.calcite;

import com.rohan.sql_query_optimizer.dto.ai.AiGeneratedQuery;
import com.rohan.sql_query_optimizer.dto.calcite.OptimizedQuery;
import com.rohan.sql_query_optimizer.service.db.DBConnectionManager;
import lombok.CustomLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * The type Query optimizer service.
 */
@Service
@CustomLog
public class QueryOptimizerService {

    private final DBConnectionManager dbConnectionManager;

    /**
     * Instantiates a new Query optimizer service.
     *
     * @param dbConnectionManager the db connection manager
     */
    @Autowired
    public QueryOptimizerService(DBConnectionManager dbConnectionManager) {
        this.dbConnectionManager = dbConnectionManager;
    }

    /**
     * Optimize optimized query.
     *
     * @param query the query
     * @return the optimized query
     * @throws Exception the exception
     */
    public OptimizedQuery optimize(AiGeneratedQuery query) throws Exception {
        return dbConnectionManager.getValidator().optimize(query);
    }
}
