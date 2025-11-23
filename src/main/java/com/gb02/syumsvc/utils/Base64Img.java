package com.gb02.syumsvc.utils;

import com.gb02.syumsvc.exceptions.UnexpectedErrorException;

public class Base64Img {
    public static String saveB64(String b64, String nick){
        String extension = b64.substring(b64.indexOf("/") + 1, b64.indexOf(";"));
        String base64Data = b64.substring(b64.indexOf(",") + 1);
        java.nio.file.Path path = java.nio.file.Paths.get("src/main/resources/static/pfp/" + nick + "." + extension);
        try {
            byte[] decodedBytes = java.util.Base64.getDecoder().decode(base64Data);
            java.nio.file.Files.createDirectories(path.getParent());
            java.nio.file.Files.write(path, decodedBytes);
        } catch (java.io.IOException e) {
            throw new UnexpectedErrorException("Error while trying to save profile picture: " + e.getMessage());
        }
        return extension;
    }

    public static String changeNick(String oldImagePath, String newNick){
        String extension = oldImagePath.substring(oldImagePath.lastIndexOf('.') + 1);
        java.nio.file.Path source = java.nio.file.Paths.get("src/main/resources/static" + oldImagePath);
        java.nio.file.Path target = java.nio.file.Paths.get("src/main/resources/static/pfp/" + newNick + "." + extension);
        try {
            java.nio.file.Files.move(source, target, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            return target.toString().replace("src/main/resources/static", "");
        } catch (java.io.IOException e) {
            throw new UnexpectedErrorException("Error while trying to rename profile picture: " + e.getMessage());
        }
    }
}
