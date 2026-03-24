package com.rohan.sql_query_optimizer.controller;

import com.rohan.sql_query_optimizer.service.schema.SchemaService;
import org.springframework.ai.chat.client.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("schema")
public class SchemaController {

    @Autowired
    private SchemaService schemaService;

    @GetMapping("/")
    public ResponseEntity<String, HttpStatus> getSchemas() {
        try {
            return new ResponseEntity<>(schemaService.getSchema(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }
}
