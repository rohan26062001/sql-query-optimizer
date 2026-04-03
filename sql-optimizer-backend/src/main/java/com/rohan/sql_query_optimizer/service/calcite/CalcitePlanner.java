package com.rohan.sql_query_optimizer.service.calcite;

import com.rohan.sql_query_optimizer.dto.ai.AiGeneratedQuery;
import com.rohan.sql_query_optimizer.dto.calcite.OptimizedQuery;
import lombok.CustomLog;
import org.apache.calcite.adapter.enumerable.EnumerableConvention;
import org.apache.calcite.adapter.jdbc.JdbcSchema;
import org.apache.calcite.adapter.jdbc.JdbcToEnumerableConverter;
import org.apache.calcite.avatica.util.Casing;
import org.apache.calcite.config.CalciteConnectionConfig;
import org.apache.calcite.config.CalciteConnectionProperty;
import org.apache.calcite.plan.*;
import org.apache.calcite.plan.volcano.AbstractConverter;
import org.apache.calcite.plan.volcano.VolcanoPlanner;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.RelRoot;
import org.apache.calcite.rel.RelShuttleImpl;
import org.apache.calcite.rel.metadata.RelMetadataQuery;
import org.apache.calcite.rel.rel2sql.RelToSqlConverter;
import org.apache.calcite.rel.rules.CoreRules;
import org.apache.calcite.schema.SchemaPlus;
import org.apache.calcite.sql.SqlDialect;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.dialect.PostgresqlSqlDialect;
import org.apache.calcite.sql.parser.SqlParseException;
import org.apache.calcite.sql.parser.SqlParser;
import org.apache.calcite.tools.*;

import javax.sql.DataSource;

/**
 * The type Calcite planner.
 */
@CustomLog
public class CalcitePlanner {

    private final FrameworkConfig config;

    /**
     * Instantiates a new Calcite planner.
     *
     * @param dataSource the data source
     * @param dbSchema   the db schema
     */
    public CalcitePlanner(DataSource dataSource, String dbSchema) {
        SchemaPlus rootSchema = Frameworks.createRootSchema(true);
        SchemaPlus jdbcSchema = rootSchema.add("db", JdbcSchema.create(rootSchema, "db", dataSource, null, dbSchema));

        CalciteConnectionConfig calciteConfig = CalciteConnectionConfig.DEFAULT.set(CalciteConnectionProperty.CASE_SENSITIVE, "false");

        this.config = Frameworks.newConfigBuilder()
                .defaultSchema(jdbcSchema)
                .parserConfig(SqlParser.config().withCaseSensitive(false).withUnquotedCasing(Casing.TO_LOWER))
                .context(Contexts.of(calciteConfig))
                .build();
    }

    /**
     * Validate string.
     *
     * @param sql the sql
     * @return the string
     */
    public String validate(String sql) {
        sql = sql.trim().replaceAll(";$", ""); // ✅ remove trailing semicolon
        try (Planner planner = Frameworks.getPlanner(config)) {
            SqlNode parsed = planner.parse(sql);
            planner.validate(parsed);
            return null;
        } catch (SqlParseException e) {
            return String.format("SYNTAX_ERROR:=> Reason: %s", e.getMessage());
        } catch (ValidationException e) {
            return String.format("SCHEMA_ERROR:=> Reason: %s", e.getMessage());
        }
    }

    /**
     * Optimize optimized query.
     *
     * @param validQuery the valid query
     * @return the optimized query
     * @throws Exception the exception
     */
    public OptimizedQuery optimize(AiGeneratedQuery validQuery) throws Exception {
        OptimizedQuery responseQuery = new OptimizedQuery();
        responseQuery.setOriginalSqlQuery(validQuery.getQuery());

        String sql = validQuery.getQuery();
        sql = sql.trim().replaceAll(";$", "");

        // Step 1 : Create planner from your config
        Planner planner = Frameworks.getPlanner(config);

        // Step 2 : Convert to RelNode
        RelNode relNode = convertToRelNode(planner, sql);
        log.debug("Logical Plan : \n{}", RelOptUtil.toString(relNode));

        // Step 3 : Optimize RelNode
        RelNode optimizeRelNode = optimizeRelNode(relNode, responseQuery);
        log.debug("Optimized Plan : \n{}", RelOptUtil.toString(optimizeRelNode));

        // Step 4 : Convert Optimized Query back to Sql
        String optimizedQuery = convertRelTreeToSql(optimizeRelNode);
        responseQuery.setOptimizedSqlQuery(optimizedQuery);
        log.debug("Optimized Query: {}", optimizedQuery);

        return responseQuery;
    }

    private RelNode convertToRelNode(Planner planner, String sql) throws SqlParseException, ValidationException, RelConversionException {

        // 1. Parse SQL → SqlNode
        SqlNode parsed = planner.parse(sql);

        // 2. Validate SQL
        SqlNode validated = planner.validate(parsed);

        // 3. Convert → RelNode
        RelRoot relRoot = planner.rel(validated);

        return relRoot.rel;
    }

    private RelNode optimizeRelNode(RelNode relNode, OptimizedQuery responseQuery) {
        responseQuery.setOriginalRelNode(relNode);

        // Step 1: Get VolcanoPlanner
        VolcanoPlanner volcanoPlanner = (VolcanoPlanner) relNode.getCluster().getPlanner();

        // Step 2: Add abstract relational rules — handles NONE convention logical nodes
        addCoreRules(volcanoPlanner);

        // Step 3: Desired traits = NONE
        RelTraitSet desiredTraits = relNode.getCluster().traitSet().replace(EnumerableConvention.INSTANCE);

        RelNode newRoot = volcanoPlanner.changeTraits(relNode, desiredTraits);

        // Step 4: Log original cost
        RelMetadataQuery mq = relNode.getCluster().getMetadataQuery();
        RelOptCost originalCost = volcanoPlanner.getCost(relNode, mq);
        log.debug("Original Cost => rows={}, cpu={}, io={}",
                originalCost != null ? originalCost.getRows() : "N/A",
                originalCost != null ? originalCost.getCpu() : "N/A",
                originalCost != null ? originalCost.getIo() : "N/A"
        );
        responseQuery.setOriginalCost(originalCost);

        try {
            volcanoPlanner.setRoot(newRoot);
            RelNode optimized = volcanoPlanner.findBestExp();

            // Step 5: Log optimized cost
            RelOptCost optimizedCost = volcanoPlanner.getCost(optimized, mq);
            log.debug("Optimized Cost => rows={}, cpu={}, io={}",
                    optimizedCost != null ? optimizedCost.getRows() : "N/A",
                    optimizedCost != null ? optimizedCost.getCpu() : "N/A",
                    optimizedCost != null ? optimizedCost.getIo() : "N/A"
            );
            responseQuery.setOptimizedCost(optimizedCost);

            if (originalCost != null && optimizedCost != null) {
                log.debug("Cost Improvement => rows={}, cpu={}, io={}",
                        originalCost.getRows() - optimizedCost.getRows(),
                        originalCost.getCpu() - optimizedCost.getCpu(),
                        originalCost.getIo() - optimizedCost.getIo()
                );
            }
            responseQuery.setCostDiff(originalCost.minus(optimizedCost));
            responseQuery.setOptimizedRelNode(optimized);
            return optimized;

        } catch (RelOptPlanner.CannotPlanException e) {
            log.debug("No optimization possible, using original plan. Reason: {}", e.getMessage());
            return relNode;
        }
    }

    private void addCoreRules(VolcanoPlanner volcanoPlanner) {
        // Filter Rules
        volcanoPlanner.addRule(CoreRules.FILTER_INTO_JOIN);
        volcanoPlanner.addRule(CoreRules.FILTER_MERGE);
        volcanoPlanner.addRule(CoreRules.FILTER_PROJECT_TRANSPOSE);
        volcanoPlanner.addRule(CoreRules.FILTER_AGGREGATE_TRANSPOSE);
        volcanoPlanner.addRule(CoreRules.FILTER_SET_OP_TRANSPOSE);
        volcanoPlanner.addRule(CoreRules.FILTER_REDUCE_EXPRESSIONS);
        volcanoPlanner.addRule(CoreRules.FILTER_CORRELATE);
        volcanoPlanner.addRule(CoreRules.FILTER_SCAN);
        volcanoPlanner.addRule(CoreRules.FILTER_TABLE_FUNCTION_TRANSPOSE);
        volcanoPlanner.addRule(CoreRules.FILTER_TO_CALC);
        volcanoPlanner.addRule(CoreRules.FILTER_CALC_MERGE);

        // Project Rules
        volcanoPlanner.addRule(CoreRules.PROJECT_MERGE);
        volcanoPlanner.addRule(CoreRules.PROJECT_REMOVE);
        volcanoPlanner.addRule(CoreRules.PROJECT_FILTER_TRANSPOSE);
        volcanoPlanner.addRule(CoreRules.PROJECT_JOIN_TRANSPOSE);
        volcanoPlanner.addRule(CoreRules.PROJECT_SET_OP_TRANSPOSE);
        volcanoPlanner.addRule(CoreRules.PROJECT_REDUCE_EXPRESSIONS);
        volcanoPlanner.addRule(CoreRules.PROJECT_TO_CALC);
        volcanoPlanner.addRule(CoreRules.PROJECT_CALC_MERGE);
        volcanoPlanner.addRule(CoreRules.PROJECT_JOIN_JOIN_REMOVE);
        volcanoPlanner.addRule(CoreRules.PROJECT_JOIN_REMOVE);

        // Join Rules
        volcanoPlanner.addRule(CoreRules.JOIN_COMMUTE);
        volcanoPlanner.addRule(CoreRules.JOIN_ASSOCIATE);
        volcanoPlanner.addRule(CoreRules.JOIN_CONDITION_PUSH);
        volcanoPlanner.addRule(CoreRules.JOIN_EXTRACT_FILTER);
        volcanoPlanner.addRule(CoreRules.JOIN_PUSH_EXPRESSIONS);
        volcanoPlanner.addRule(CoreRules.JOIN_PUSH_TRANSITIVE_PREDICATES);
        volcanoPlanner.addRule(CoreRules.JOIN_REDUCE_EXPRESSIONS);
        volcanoPlanner.addRule(CoreRules.JOIN_LEFT_UNION_TRANSPOSE);
        volcanoPlanner.addRule(CoreRules.JOIN_RIGHT_UNION_TRANSPOSE);
        volcanoPlanner.addRule(CoreRules.JOIN_TO_SEMI_JOIN);
        volcanoPlanner.addRule(CoreRules.JOIN_ADD_REDUNDANT_SEMI_JOIN);

        // Aggregate Rules
        volcanoPlanner.addRule(CoreRules.AGGREGATE_REMOVE);
        volcanoPlanner.addRule(CoreRules.AGGREGATE_MERGE);
        volcanoPlanner.addRule(CoreRules.AGGREGATE_PROJECT_MERGE);
        volcanoPlanner.addRule(CoreRules.AGGREGATE_FILTER_TRANSPOSE);
        volcanoPlanner.addRule(CoreRules.AGGREGATE_UNION_TRANSPOSE);
        volcanoPlanner.addRule(CoreRules.AGGREGATE_UNION_AGGREGATE);
        volcanoPlanner.addRule(CoreRules.AGGREGATE_REDUCE_FUNCTIONS);
        volcanoPlanner.addRule(CoreRules.AGGREGATE_EXPAND_DISTINCT_AGGREGATES);
        volcanoPlanner.addRule(CoreRules.AGGREGATE_EXPAND_DISTINCT_AGGREGATES_TO_JOIN);
        volcanoPlanner.addRule(CoreRules.AGGREGATE_JOIN_TRANSPOSE);
        volcanoPlanner.addRule(CoreRules.AGGREGATE_JOIN_TRANSPOSE_EXTENDED);

        // Sort Rules
        volcanoPlanner.addRule(CoreRules.SORT_REMOVE);
        volcanoPlanner.addRule(CoreRules.SORT_REMOVE_CONSTANT_KEYS);
        volcanoPlanner.addRule(CoreRules.SORT_UNION_TRANSPOSE);
        volcanoPlanner.addRule(CoreRules.SORT_UNION_TRANSPOSE_MATCH_NULL_FETCH);
        volcanoPlanner.addRule(CoreRules.SORT_JOIN_TRANSPOSE);
        volcanoPlanner.addRule(CoreRules.SORT_PROJECT_TRANSPOSE);
        volcanoPlanner.addRule(CoreRules.SORT_JOIN_COPY);

        // Calc Rules
        volcanoPlanner.addRule(CoreRules.CALC_MERGE);
        volcanoPlanner.addRule(CoreRules.CALC_REMOVE);
        volcanoPlanner.addRule(CoreRules.CALC_REDUCE_EXPRESSIONS);
        volcanoPlanner.addRule(CoreRules.CALC_SPLIT);

        // Union Rules
        volcanoPlanner.addRule(CoreRules.UNION_MERGE);
        volcanoPlanner.addRule(CoreRules.UNION_REMOVE);
        volcanoPlanner.addRule(CoreRules.UNION_PULL_UP_CONSTANTS);
        volcanoPlanner.addRule(CoreRules.UNION_TO_DISTINCT);
        volcanoPlanner.addRule(CoreRules.INTERSECT_TO_DISTINCT);
        volcanoPlanner.addRule(CoreRules.MINUS_TO_DISTINCT);

        // Semi Join Rules
        volcanoPlanner.addRule(CoreRules.SEMI_JOIN_FILTER_TRANSPOSE);
        volcanoPlanner.addRule(CoreRules.SEMI_JOIN_PROJECT_TRANSPOSE);
        volcanoPlanner.addRule(CoreRules.SEMI_JOIN_JOIN_TRANSPOSE);
        volcanoPlanner.addRule(CoreRules.SEMI_JOIN_REMOVE);

        // Converter Rules
        volcanoPlanner.addRule(AbstractConverter.ExpandConversionRule.INSTANCE);
    }

    private String convertRelTreeToSql(RelNode optimizeRelNode) {

        // ✅ Strip JdbcToEnumerableConverter nodes before converting to SQL
        RelNode cleanedRelNode = optimizeRelNode.accept(new RelShuttleImpl() {
            @Override
            public RelNode visit(RelNode other) {
                if (other instanceof JdbcToEnumerableConverter) {
                    // Skip the converter, go directly to its input
                    return other.getInput(0).accept(this);
                }
                return super.visit(other);
            }
        });

        // 1. Define Postgres Dialect
        SqlDialect postgresDialect = PostgresqlSqlDialect.DEFAULT;

        // 2. Initialize Converter
        RelToSqlConverter converter = new RelToSqlConverter(postgresDialect);

        // 3. Visit the root RelNode
        SqlNode sqlNode = converter.visitRoot(cleanedRelNode).asStatement();

        // 4. Generate SQL String
        return sqlNode.toSqlString(postgresDialect).getSql();
    }
}
