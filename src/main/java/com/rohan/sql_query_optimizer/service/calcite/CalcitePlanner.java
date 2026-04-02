package com.rohan.sql_query_optimizer.service.calcite;

import com.rohan.sql_query_optimizer.dto.ai.AiGeneratedQuery;
import com.rohan.sql_query_optimizer.dto.calcite.OptimizedQuery;
import lombok.CustomLog;
import org.apache.calcite.adapter.jdbc.JdbcSchema;
import org.apache.calcite.avatica.util.Casing;
import org.apache.calcite.config.CalciteConnectionConfig;
import org.apache.calcite.config.CalciteConnectionProperty;
import org.apache.calcite.plan.Contexts;
import org.apache.calcite.schema.SchemaPlus;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.parser.SqlParseException;
import org.apache.calcite.sql.parser.SqlParser;
import org.apache.calcite.tools.FrameworkConfig;
import org.apache.calcite.tools.Frameworks;
import org.apache.calcite.tools.Planner;
import org.apache.calcite.tools.ValidationException;

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
        SchemaPlus jdbcSchema = rootSchema.add(
                "db",
                JdbcSchema.create(rootSchema, "db", dataSource, null, dbSchema)
        );

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
     * @param aiGeneratedQuery the ai generated query
     * @return the optimized query
     */
    public OptimizedQuery optimize(AiGeneratedQuery aiGeneratedQuery) {
        String sql = aiGeneratedQuery.getQuery();
        sql = sql.trim().replaceAll(";$", "");

        // Need to implement this

        return new OptimizedQuery(sql);
    }
}
