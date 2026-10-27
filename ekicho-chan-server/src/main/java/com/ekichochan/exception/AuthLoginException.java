package com.ekichochan.exception;

public class AuthLoginException extends RuntimeException {
    public AuthLoginException() {
        super("Authentication failed");
    }
}
