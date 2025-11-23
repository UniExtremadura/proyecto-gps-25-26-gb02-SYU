package com.gb02.syumsvc.exceptions;

/**
 * Exception thrown when attempting to remove a favorite that doesn't exist.
 */
public class FavNotFoundException extends Exception {
    
    public FavNotFoundException(String message) {
        super(message);
    }
}
