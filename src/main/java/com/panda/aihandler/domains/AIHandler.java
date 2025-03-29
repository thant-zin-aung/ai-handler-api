package com.panda.aihandler.domains;

import com.panda.aihandler.repositories.SupabaseRepository;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Component
public class AIHandler {
    private final SupabaseRepository supabaseRepository = new SupabaseRepository();
    public String getAIResponse(boolean asTranslator, String prompt) throws IOException {
        String apiId = null;
        try {
            Map<String, String> apiMap = supabaseRepository.fetchAIApiKeyAndUrl("openrouter");
            apiId = apiMap.get("apiId");
            String apiUrl = apiMap.get("apiUrl");
            String apiKey = apiMap.get("apiKey");
            String aiModel = apiMap.get("aiModel");
            String aiStatus = apiMap.get("aiStatus");
            RestTemplate restTemplate = new RestTemplate();
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("model", aiModel);
            jsonBody.put("messages", getAIMessages(asTranslator, prompt));
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer ".concat(apiKey));
            HttpEntity<String> requestEntity = new HttpEntity<>(jsonBody.toString(), headers);
            ResponseEntity<String> responseEntity = restTemplate.exchange(apiUrl, HttpMethod.POST, requestEntity, String.class);
            String responseBody = responseEntity.getBody();
            return extractAIResponse(responseBody);
        } catch (Exception e) {
            System.out.println("AI Limitation Error: "+e.getMessage());
            try {
                supabaseRepository.updateData("id=eq.".concat(apiId), "status", "stopped");
            } catch (Exception ee) {
                return null;
            }
            return null;
        }
    }
    public String getGeminiAIResponse(boolean asTranslator, String prompt) throws IOException {
        String apiId = null;
        try {
            Map<String, String> apiMap = supabaseRepository.fetchAIApiKeyAndUrl("gemini");
            apiId = apiMap.get("apiId");
            String apiUrl = apiMap.get("apiUrl");
            String apiKey = apiMap.get("apiKey");
            String geminiEndpointPoint = apiUrl+apiKey;
            RestTemplate restTemplate = new RestTemplate();
            JSONObject jsonBody = new JSONObject();
            Map<String, Object> textWrapper = new LinkedHashMap<>();
            Map<String, Object> partsWrapper = new LinkedHashMap<>();
            textWrapper.put("text", asTranslator ? "Translate to "+(BurmeseCharacterDetector.check(prompt)?"English":"Burmese")+" language without any extra text or explanations -> ".concat(prompt) : prompt);
            partsWrapper.put("parts", textWrapper);
            jsonBody.put("contents", partsWrapper);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> requestEntity = new HttpEntity<>(jsonBody.toString(), headers);
            ResponseEntity<String> responseEntity = restTemplate.exchange(geminiEndpointPoint, HttpMethod.POST, requestEntity, String.class);
            String responseBody = responseEntity.getBody();
            return extractGeminiAIResponse(responseBody);
        } catch (Exception e) {
            System.out.println("AI Limitation Error: "+e.getMessage());
            try {
                supabaseRepository.updateData("id=eq.".concat(apiId), "status", "stopped");
            } catch (Exception ee) {
                return null;
            }
            return null;
        }
    }

    private List<AIMessage> getAIMessages(boolean asTranslator, String prompt) {
        List<AIMessage> aiMessages = new LinkedList<>();
        AIMessage aiMessage = new AIMessage();
        List<AIMessage.AIContent> aiContents = new LinkedList<>();
        AIMessage.AIContent aiContent = new AIMessage.AIContent();
        aiContent.setType("text");
        aiContent.setText(asTranslator ? "Translate to "+(BurmeseCharacterDetector.check(prompt)?"English":"Burmese")+" language without any extra text or explanations -> ".concat(prompt) : prompt);
        aiContents.add(aiContent);
        aiMessage.setRole("user");
        aiMessage.setContent(aiContents);
        aiMessages.add(aiMessage);
        return aiMessages;
    }

    private String extractAIResponse(String responseBody) {
        JSONObject jsonObject = new JSONObject(responseBody);
        return jsonObject.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content");
    }

    private String extractGeminiAIResponse(String responseBody) {
        JSONObject jsonObject = new JSONObject(responseBody);
        return jsonObject.getJSONArray("candidates").getJSONObject(0).getJSONObject("content").getJSONArray("parts").getJSONObject(0).getString("text");
    }
}
