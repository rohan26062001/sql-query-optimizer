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

/**
 * The type Base controller.
 */
@RestController
public class BaseController {

    private final BaseService baseService;

    /**
     * Instantiates a new Base controller.
     *
     * @param baseService the base service
     */
    @Autowired
    public BaseController(BaseService baseService) {
        this.baseService = baseService;
    }

    /**
     * Execute response entity.
     *
     * @param userInput the user input
     * @return the response entity
     */
    @PostMapping("/execute")
    public ResponseEntity<UserOutput, HttpStatus> execute(@RequestBody UserInput userInput) {
        try {
            return new ResponseEntity<>(baseService.execute(userInput), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new UserOutput(), HttpStatus.NOT_ACCEPTABLE);
        }
    }
}
