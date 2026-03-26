package com.rohan.sql_query_optimizer.controller.query.ai;

import com.rohan.sql_query_optimizer.dto.query.InputQuery;
import com.rohan.sql_query_optimizer.dto.query.QueryOutput;
import com.rohan.sql_query_optimizer.service.query.QueryGenAiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/ai")
public class QueryGenController {

    @Autowired
    private QueryGenAiService queryGenAiService;

    @PostMapping("/generate-sql")
    public QueryOutput generateQuery(@RequestBody InputQuery userInput) throws SQLException {
        return queryGenAiService.generateQuery(userInput);
    }

    @PostMapping("/generate-sqls")
    public List<QueryOutput> generateQueries(@RequestBody List<InputQuery> userInput) throws SQLException {
        return queryGenAiService.generateQueries(userInput);
    }
}
