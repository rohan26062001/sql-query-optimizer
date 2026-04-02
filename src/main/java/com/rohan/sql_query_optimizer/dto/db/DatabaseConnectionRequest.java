package com.rohan.sql_query_optimizer.dto.db;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The type Database connection request.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DatabaseConnectionRequest {
    private String host;
    private String port;
    private String database;
    private String username;
    private String password;
}
