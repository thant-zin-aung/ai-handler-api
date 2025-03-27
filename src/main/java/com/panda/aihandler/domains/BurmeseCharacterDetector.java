package com.panda.aihandler.domains;

import java.util.regex.Pattern;

public class BurmeseCharacterDetector {
    public static boolean check(String text) {
        // Regular expression for Burmese characters (Myanmar script)
        String burmeseRegex = "[\\u1000-\\u109F]";
        return Pattern.compile(burmeseRegex).matcher(text).find();
    }
}
