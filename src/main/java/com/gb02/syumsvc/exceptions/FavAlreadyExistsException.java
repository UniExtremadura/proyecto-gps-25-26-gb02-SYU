package com.gb02.syumsvc.exceptions;

/**
 * Exception thrown when attempting to add a favorite that already exists.
 */
public class FavAlreadyExistsException extends Exception {
    
    public FavAlreadyExistsException(String message) {
        super(message);
    }
}
