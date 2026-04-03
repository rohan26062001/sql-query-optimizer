package com.rohan.sql_query_optimizer.dto.calcite;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.calcite.plan.RelOptCost;
import org.apache.calcite.rel.RelNode;

/**
 * The type Optimized query.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OptimizedQuery {
    private String originalSqlQuery;
    private RelNode originalRelNode;
    private String optimizedSqlQuery;
    private RelNode optimizedRelNode;
    private RelOptCost originalCost;
    private RelOptCost optimizedCost;
    private RelOptCost costDiff;
}
