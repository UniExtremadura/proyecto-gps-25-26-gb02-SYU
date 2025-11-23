package com.gb02.syumsvc.exceptions;

public class DupedEmailException extends RuntimeException {
    public DupedEmailException(String message) {
        super(message);
    }
}
