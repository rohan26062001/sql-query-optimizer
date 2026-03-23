package com.rohan.sql_query_optimizer.dto.db;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DatabaseConnectionParams {
    private String host;
    private String port;
    private String database;
    private String username;
    private String password;
}
