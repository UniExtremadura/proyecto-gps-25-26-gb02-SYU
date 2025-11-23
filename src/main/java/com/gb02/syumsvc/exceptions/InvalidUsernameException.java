package com.gb02.syumsvc.exceptions;

public class InvalidUsernameException extends RuntimeException {
    public InvalidUsernameException() {
        super("Username can't contain special characters or spaces, and must be between 3 and 30 characters long.");
    }
}
