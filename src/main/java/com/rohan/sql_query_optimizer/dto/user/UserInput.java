package com.rohan.sql_query_optimizer.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The type User input.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInput {
    private String userMessage;
    private String additionalInputs;
}
