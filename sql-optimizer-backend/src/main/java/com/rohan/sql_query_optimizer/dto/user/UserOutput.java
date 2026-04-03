package com.rohan.sql_query_optimizer.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.calcite.plan.RelOptCost;

/**
 * The type User output.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserOutput {
    private String userInput;
    private String aiGeneratedQuery;
    private String optimizedSqlQuery;
    private RelOptCost originalCost;
    private RelOptCost optimizedCost;
    private RelOptCost costDifference;
    private String whatIsOptimized;
    private String resultSet;
}
