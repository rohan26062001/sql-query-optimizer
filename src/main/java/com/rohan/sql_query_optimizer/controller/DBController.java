package com.rohan.sql_query_optimizer.controller;

import com.rohan.sql_query_optimizer.dto.db.DatabaseConnectionParams;
import com.rohan.sql_query_optimizer.dto.db.DatabaseConnectionResponse;
import com.rohan.sql_query_optimizer.enums.db.DBConnectionStatus;
import com.rohan.sql_query_optimizer.service.db.DBService;
import org.springframework.ai.chat.client.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/db")
public class DBController {

    @Autowired
    private DBService dbService;

    @PostMapping("/connect")
    public ResponseEntity<DatabaseConnectionResponse, HttpStatus> connect(@RequestBody DatabaseConnectionParams params) {
        try {
            return new ResponseEntity<>(dbService.connect(params), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new DatabaseConnectionResponse(DBConnectionStatus.FAIL, e.getMessage()), HttpStatus.UNAUTHORIZED);
        }
    }
}
