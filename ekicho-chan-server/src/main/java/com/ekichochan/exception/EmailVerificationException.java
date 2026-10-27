package com.ekichochan.exception;

public class EmailVerificationException extends RuntimeException {
    public EmailVerificationException() {
        super("Email verification token is invalid or expired");
    }
}
