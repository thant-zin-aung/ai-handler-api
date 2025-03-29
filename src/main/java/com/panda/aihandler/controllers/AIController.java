package com.panda.aihandler.controllers;

import com.panda.aihandler.domains.AIHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/v1/handler")
public class AIController {
    private AIHandler aiHandler;
    public AIController(AIHandler aiHandler) {
        this.aiHandler = aiHandler;
    }
    @GetMapping("/translate")
    public ResponseEntity<Object> getTranslatedMessage(@RequestParam("message") String message) {
        try {
            return ResponseEntity.ok(aiHandler.getAIResponse(true, message));
        } catch (IOException e) {
            return ResponseEntity.ok(e.getMessage());
        }
    }
}
