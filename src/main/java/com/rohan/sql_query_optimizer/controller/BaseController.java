package com.rohan.sql_query_optimizer.controller;

import com.rohan.sql_query_optimizer.dto.user.UserInput;
import com.rohan.sql_query_optimizer.dto.user.UserOutput;
import com.rohan.sql_query_optimizer.service.BaseService;
import org.springframework.ai.chat.client.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BaseController {

    @Autowired
    private BaseService baseService;

    @PostMapping("/execute")
    public ResponseEntity<UserOutput, HttpStatus> execute(@RequestBody UserInput userInput) {
        try {
            return new ResponseEntity<>(baseService.execute(userInput), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new UserOutput(e.getMessage()), HttpStatus.NOT_ACCEPTABLE);
        }
    }
}
