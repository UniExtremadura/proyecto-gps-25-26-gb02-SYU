package com.gb02.syumsvc.utils;

import java.util.HashMap;
import java.util.Map;

public class Response {
    public static Map<String, Object> getErrorResponse(int code, String message) {
        Map<String, Object> errorMap = new HashMap<>();
        errorMap.put("code", code);
        errorMap.put("message", message);
        return errorMap;
    }

    public static Map<String, Object> getOnlyMessage(String message){
        Map<String, Object> messageMap = new HashMap<>();
        messageMap.put("message", message);
        return messageMap;
    }
}
