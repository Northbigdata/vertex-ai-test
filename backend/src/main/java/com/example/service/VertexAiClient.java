package com.example.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import java.time.Duration;
import java.util.*;

@Service
public class VertexAiClient {

    @Value("${project.id}")
    private String projectId;

    @Value("${vertex.location:us-central1}")
    private String location;

    @Value("${vertex.model:imagen-3.0-generate-002}")
    private String modelVersion;

    @Value("${gcs.output.bucket}")
    private String outputBucket;

    private final ObjectMapper mapper = new ObjectMapper();

    public Map<String,Object> generateImage(String prompt) throws Exception {
        String endpoint = String.format("https://%s-aiplatform.googleapis.com/v1/projects/%s/locations/%s/publishers/google/models/%s:predict",
                location, projectId, location, modelVersion);

        // 1) ADC로 액세스 토큰 얻기
        GoogleCredentials credentials = GoogleCredentials.getApplicationDefault()
                .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));
        credentials.refreshIfExpired();
        String token = credentials.getAccessToken().getTokenValue();

        // 2) request body. storageUri를 주면 GCS에 결과 저장 가능
        Map<String,Object> req = new HashMap<>();
        Map<String,String> instance = Map.of("prompt", prompt);
        req.put("instances", List.of(instance));
        Map<String,Object> params = new HashMap<>();
        params.put("sampleCount", 1);
        // storageUri: e.g. gs://your-bucket/path/
        params.put("storageUri", String.format("gs://%s/vertex-output/", outputBucket));
        req.put("parameters", params);

        String body = mapper.writeValueAsString(req);

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json; charset=utf-8")
                .timeout(Duration.ofSeconds(120))
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpClient client = HttpClient.newBuilder().build();
        HttpResponse<String> res = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        if (res.statusCode() >= 300) {
            throw new RuntimeException("Vertex AI error: " + res.statusCode() + " " + res.body());
        }

        JsonNode json = mapper.readTree(res.body());
        // 응답 처리: 모델이 storageUri를 사용해 이미지를 GCS에 저장하면
        // 응답의 predictions/.. 혹은 다른 필드에서 location을 찾을 수 있음.
        // 단순히 여기서는 응답 전체를 반환
        Map<String,Object> result = new HashMap<>();
        result.put("raw", json);
        // (옵션) 반환에 GCS URL을 추가하려면 generation naming convention을 사용
        // 예: gs://bucket/vertex-output/<자동 생성 파일명>.png  (실환경에서 콘솔 확인)
		JsonNode predictions = json.path("predictions");
        if (predictions.isArray() && predictions.size() > 0) {
            String gcsUri = predictions.get(0).path("gcsUri").asText(null);
            if (gcsUri != null && !gcsUri.isBlank()) {
             // GCS URI → HTTPS URL 변환
                String httpsUrl = gcsUri.replace("gs://", "https://storage.googleapis.com/");
                result.put("imageUrl", httpsUrl);
    }
}			
        return result;
    }
}
