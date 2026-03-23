package com.rohan.sql_query_optimizer.dto.db;

import com.rohan.sql_query_optimizer.enums.db.DBConnectionStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DatabaseConnectionResponse {
    private DBConnectionStatus status;
    private String message;
}
