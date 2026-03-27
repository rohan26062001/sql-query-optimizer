package com.rohan.sql_query_optimizer.service.query;

import com.rohan.sql_query_optimizer.dto.ai.AiGeneratedQuery;
import com.rohan.sql_query_optimizer.dto.user.UserInput;
import com.rohan.sql_query_optimizer.service.schema.SchemaService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
        return generateAiGeneratedQueryForSingleQuery(userInput);
    }

    public List<AiGeneratedQuery> generateQueries(List<UserInput> userInput) throws SQLException {
        List<AiGeneratedQuery> AiGeneratedQuery = new ArrayList<>();
        for(UserInput inputQuery : userInput) {
            AiGeneratedQuery.add(generateAiGeneratedQueryForSingleQuery(inputQuery));
        }
        return AiGeneratedQuery;
    }

    private AiGeneratedQuery generateAiGeneratedQueryForSingleQuery(UserInput userInput) throws SQLException {
        Map<String, Object> promptReplacementMap = new HashMap<>();
        promptReplacementMap.put("schema", schemaService.getSchema());
        promptReplacementMap.put("userMessage", userInput.getUserMessage());
        promptReplacementMap.put("additionalInputs", userInput.getAdditionalInputs());
        String promtTemplateString = getPrompt();

        PromptTemplate promptTemplate = new PromptTemplate(promtTemplateString);
        Prompt prompt = promptTemplate.create(promptReplacementMap);
        return new AiGeneratedQuery(chatClient.prompt(prompt).call().content());
    }

    private String getPrompt() {
        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("prompts/sql_prompt.txt");
            if (inputStream == null)
                throw new RuntimeException("Prompt file not found");

            return new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.joining("\n"));
        } catch (Exception e) {
            throw new RuntimeException("Error loading prompt file", e);
        }
    }
}
