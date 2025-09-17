package com.example.controller;

import com.example.service.VertexAiClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {
        "https://frontend-service-268824299811.asia-northeast3.run.app" // 프론트엔드 Cloud Run URL
})
public class GenerateController {

    private final VertexAiClient client;

    public GenerateController(VertexAiClient client) {
        this.client = client;
    }

    @PostMapping("/generate")
    public ResponseEntity<Map<String, Object>> generate(@RequestBody Map<String, String> body) {
        try {
            String prompt = body.get("prompt");
            if (prompt == null || prompt.isBlank()) {
                throw new ResponseStatusException(
                        org.springframework.http.HttpStatus.BAD_REQUEST,
                        "prompt is required"
                );
            }

            // modelName, bucket 등은 VertexAiClient 내부에서 env 로 주입
            Map<String, Object> result = client.generateImage(prompt);
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(
                    org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to generate image: " + e.getMessage()
            );
        }
    }
}
