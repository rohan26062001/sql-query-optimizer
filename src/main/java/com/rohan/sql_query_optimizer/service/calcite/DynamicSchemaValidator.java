package com.rohan.sql_query_optimizer.service.calcite;

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
 * The type Dynamic schema validator.
 */
public class DynamicSchemaValidator {

    private final FrameworkConfig config;

    /**
     * Instantiates a new Dynamic schema validator.
     *
     * @param dataSource the data source
     * @param dbSchema   the db schema
     */
    public DynamicSchemaValidator(DataSource dataSource, String dbSchema) {
        SchemaPlus rootSchema = Frameworks.createRootSchema(true);
        SchemaPlus jdbcSchema = rootSchema.add(
                "db",
                JdbcSchema.create(rootSchema, "db", dataSource, null, dbSchema)
        );

        CalciteConnectionConfig calciteConfig = CalciteConnectionConfig.DEFAULT.set(CalciteConnectionProperty.CASE_SENSITIVE, "false");

        this.config = Frameworks.newConfigBuilder()
                .defaultSchema(jdbcSchema)
                .parserConfig(
                        SqlParser.config()
                                .withCaseSensitive(false)
                                .withUnquotedCasing(Casing.TO_LOWER)
                )
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
}
