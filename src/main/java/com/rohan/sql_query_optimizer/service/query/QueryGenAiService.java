package com.rohan.sql_query_optimizer.service.query;

import com.rohan.sql_query_optimizer.dto.ai.AiGeneratedQuery;
import com.rohan.sql_query_optimizer.dto.user.UserInput;
import com.rohan.sql_query_optimizer.service.schema.SchemaService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class QueryGenAiService {

    private SchemaService schemaService;
    private ChatClient chatClient;

    public QueryGenAiService(SchemaService schemaService, ChatClient.Builder chatClientBuilder) {
        this.schemaService = schemaService;
        this.chatClient = chatClientBuilder.build();
    }

    public AiGeneratedQuery generateQuery(UserInput userInput) throws SQLException {
        Map<String, Object> promptReplacementMap = new HashMap<>();
        promptReplacementMap.put("schema", schemaService.getSchema());
        promptReplacementMap.put("userMessage", userInput.getUserMessage());
        promptReplacementMap.put("additionalInputs", userInput.getAdditionalInputs());
        String promtTemplateString = getPrompt("query_generation_prompt.txt");

        PromptTemplate promptTemplate = new PromptTemplate(promtTemplateString);
        Prompt prompt = promptTemplate.create(promptReplacementMap);
        return new AiGeneratedQuery(chatClient.prompt(prompt).call().content());
    }

    public AiGeneratedQuery rectifyQuery(UserInput userInput, AiGeneratedQuery wrongQuery, String validationError) throws SQLException {
        Map<String, Object> promptReplacementMap = new HashMap<>();
        promptReplacementMap.put("schema", schemaService.getSchema());
        promptReplacementMap.put("userMessage", userInput.getUserMessage());
        promptReplacementMap.put("invalidQuery", wrongQuery.getQuery());
        promptReplacementMap.put("validationError", validationError);
        String promtTemplateString = getPrompt("query_rectifier_prompt.txt");

        PromptTemplate promptTemplate = new PromptTemplate(promtTemplateString);
        Prompt prompt = promptTemplate.create(promptReplacementMap);
        return new AiGeneratedQuery(chatClient.prompt(prompt).call().content());
    }

    private String getPrompt(String promptFileName) {
        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(String.format("prompts/%s", promptFileName));
            if (inputStream == null)
                throw new FileNotFoundException(String.format("Prompt file %s not found", promptFileName));
            return new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.joining("\n"));
        } catch (Exception e) {
            throw new RuntimeException("Error loading prompt file", e);
        }
    }
}
