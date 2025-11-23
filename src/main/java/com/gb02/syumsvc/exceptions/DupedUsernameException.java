package com.gb02.syumsvc.exceptions;

public class DupedUsernameException extends RuntimeException {
    public DupedUsernameException(String message) {
        super(message);
    }
}
