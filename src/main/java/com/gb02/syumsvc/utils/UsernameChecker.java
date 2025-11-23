package com.gb02.syumsvc.utils;

public class UsernameChecker {
    public static boolean isValidUsername(String username) {
        return username != null && username.matches("^[a-zA-Z0-9_]+$") && username.length() >= 3 && username.length() <= 30;
    }
}
