package com.panda.aihandler.domains;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AIMessage {
    @Getter
    @Setter
    public static class AIContent {
        private String type;
        private String text;
    }

    private String role;
    private List<AIContent> content;
}
