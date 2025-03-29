package com.panda.aihandler;

import com.panda.aihandler.domains.AIHandler;
import com.panda.aihandler.repositories.SupabaseRepository;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        AIHandler aiHandler = new AIHandler();
//        System.out.println("Response: "+aiHandler.getAIResponse(true, "Hey! What's up?"));
        System.out.println("Response: "+aiHandler.getAIResponse(true, "What if I want to learn new programming languges?"));

        SupabaseRepository repo = new SupabaseRepository();
//        repo.fetchAllData("select=*");
//        repo.fetchAllData("status=eq.running&select=*");
//        repo.fetchAllData("status=eq.running&select=*&limit=1");
//        repo.updateData("model=eq.testing", "status", "pushing");
//        repo.updateAllStatusToRunning();
//        repo.fetchGeminiApiKey();
//        Map<String, String> apiMap = repo.fetchGeminiApiKeyAndUrl();
//        System.out.println(apiMap.get("apiUrl"));
//        System.out.println(apiMap.get("apiKey"));
    }
}
