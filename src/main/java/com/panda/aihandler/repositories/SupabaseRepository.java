package com.panda.aihandler.repositories;

import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class SupabaseRepository {
    private static final String SUPABASE_URL = "https://opuzrmqdjjncvnnosyuv.supabase.co/rest/v1/";
    private static final String SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im9wdXpybXFkampuY3Zubm9zeXV2Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDA3MTQ0MDAsImV4cCI6MjA1NjI5MDQwMH0.xG8uMzecjw9XJEk6wKRQKcMzJJyAVIomG8FUZKZTwwM";
    private OkHttpClient client = new OkHttpClient();

    public String fetchAllData(String expression) throws IOException {
        String url = SUPABASE_URL + "ai_handler?"+expression;

        Request request = new Request.Builder()
                .url(url)
                .header("apikey", SUPABASE_ANON_KEY)
                .header("Authorization", "Bearer " + SUPABASE_ANON_KEY)
                .header("Content-Type", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                return response.body().string();
            } else {
                System.out.println("Request failed with code: " + response.code());
            }
        }
        return null;
    }

    public void updateData(String condition, String currentValue, String updateValue) throws IOException {
        String url = SUPABASE_URL + "ai_handler?" + condition;
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(currentValue, updateValue);
        RequestBody body = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .url(url)
                .header("apikey", SUPABASE_ANON_KEY)
                .header("Authorization", "Bearer " + SUPABASE_ANON_KEY)
                .header("Content-Type", "application/json")
                .header("Prefer", "return=minimal")
                .patch(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                System.out.println("Update Response: " + response.body().string());
            } else {
                System.out.println("Request failed with code: " + response.code());
            }
        }
    }

    public void updateAllStatusToRunning() throws IOException {
        updateData("status=eq.stopped", "status", "running");
    }

    public Map<String, String> fetchAIApiKeyAndUrl(String vendor) throws IOException {
        Map<String, String> apiMap = new LinkedHashMap<>();
        String expression = "vendor=eq.".concat(vendor).concat("&status=eq.running&select=*&limit=1");
//        if(vendor.equalsIgnoreCase("gemini")) {
//            expression = "vendor=eq.gemini&status=eq.running&select=*&limit=1";
//        } else if(vendor.equalsIgnoreCase("openrouter")) {
//            expression = "vendor=eq.openrouter&status=eq.running&select=*&limit=1";
//        }
        try {
            String responseBody = fetchAllData(expression);
            JSONArray jsonResponse = new JSONArray(responseBody);
            if(jsonResponse.toList().size()==0) {
                updateData("vendor=eq.".concat(vendor), "status", "running");
                responseBody = fetchAllData(expression);
                jsonResponse = new JSONArray(responseBody);
            }
            apiMap.put("apiId", String.valueOf(jsonResponse.getJSONObject(0).getInt("id")));
            apiMap.put("apiUrl", jsonResponse.getJSONObject(0).getString("api_url"));
            apiMap.put("apiKey", jsonResponse.getJSONObject(0).getString("api_key"));
            apiMap.put("aiModel", jsonResponse.getJSONObject(0).getString("model"));
            apiMap.put("aiStatus", jsonResponse.getJSONObject(0).getString("status"));
            return apiMap;
        } catch (Exception e) {
            System.out.println("AI Limitation Error: "+e.getMessage());
            return null;
        }
    }
}