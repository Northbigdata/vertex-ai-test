package com.example.controller;

import com.example.service.VertexAiClient;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class GenerateController {

    private final VertexAiClient client;
    public GenerateController(VertexAiClient client) {
        this.client = client;
    }

    @PostMapping("/generate")
    public Map<String, Object> generate(@RequestBody Map<String,String> body) throws Exception {
        String prompt = body.get("prompt");
        // modelName, bucket 등은 env로 주입
        return client.generateImage(prompt);
    }
}
