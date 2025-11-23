package com.gb02.syumsvc.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class SecureUtils {
    public static String hashPassword(String password){
        if (password == null) return null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashed = md.digest(password.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(2 * hashed.length);
            for (byte b : hashed) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean verifyPassword(String password, String hashedPassword){
        if (password == null || hashedPassword == null) return false;
        String hashedInput = hashPassword(password);
        return hashedInput.equals(hashedPassword);
    }

    public static String generateSessionToken(){
        SecureRandom random = new java.security.SecureRandom();
        byte[] tokenBytes = new byte[64];
        random.nextBytes(tokenBytes);
        StringBuilder sb = new StringBuilder(2 * tokenBytes.length);
        for (byte b : tokenBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
