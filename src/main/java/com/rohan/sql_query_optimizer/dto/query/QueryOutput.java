package com.rohan.sql_query_optimizer.dto.query;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QueryOutput {
    private String inputQuery;
    private String outputSqlQuery;
}
