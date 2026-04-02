package com.rohan.sql_query_optimizer.controller;

import com.rohan.sql_query_optimizer.dto.db.DatabaseConnectionRequest;
import com.rohan.sql_query_optimizer.dto.db.DatabaseConnectionResponse;
import com.rohan.sql_query_optimizer.enums.db.DBConnectionStatus;
import com.rohan.sql_query_optimizer.service.db.DBService;
import org.springframework.ai.chat.client.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * The type Db controller.
 */
@RestController
@RequestMapping("/db")
public class DBController {

    private final DBService dbService;

    /**
     * Instantiates a new Db controller.
     *
     * @param dbService the db service
     */
    @Autowired
    public DBController(DBService dbService) {
        this.dbService = dbService;
    }

    /**
     * Connect response entity.
     *
     * @param params the params
     * @return the response entity
     */
    @PostMapping("/connect")
    public ResponseEntity<DatabaseConnectionResponse, HttpStatus> connect(@RequestBody DatabaseConnectionRequest params) {
        try {
            return new ResponseEntity<>(dbService.connect(params), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new DatabaseConnectionResponse(DBConnectionStatus.FAIL, e.getMessage()), HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * Close response entity.
     *
     * @return the response entity
     */
    @GetMapping("/close")
    public ResponseEntity<String, HttpStatus> close() {
        try {
            return new ResponseEntity<>(dbService.close(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
